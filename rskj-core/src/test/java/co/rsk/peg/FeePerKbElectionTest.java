/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.peg;

import co.rsk.bitcoinj.core.Coin;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FeePerKbElectionTest {
    @Test
    public void electionWithNoVotes() {
        AddressBasedAuthorizer authorizer = mock(AddressBasedAuthorizer.class);
        when(authorizer.getRequiredAuthorizedKeys())
                .thenReturn(2);
        FeePerKbElection election = new FeePerKbElection(authorizer);
        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getVotes(), is(empty()));
    }

    @Test
    public void electionWithTwoVotesHasWinner() {
        AddressBasedAuthorizer authorizer = mock(AddressBasedAuthorizer.class);
        when(authorizer.getRequiredAuthorizedKeys())
                .thenReturn(2);
        Coin expectedFee = Coin.FIFTY_COINS;
        HashSet<TxSender> votes = new HashSet<>(Arrays.asList(
                new TxSender(new byte[] {0x01}),
                new TxSender(new byte[] {0x02})
        ));
        FeePerKbElection election = new FeePerKbElection(authorizer, votes, expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(true));
        Assert.assertThat(election.getWinner().get(), is(expectedFee));
        Assert.assertThat(election.getFeePerKb(), is(expectedFee));
        Assert.assertThat(election.getVotes().size(), is(2));
    }

    @Test
    public void voteTheSameTwiceToWin() {
        AddressBasedAuthorizer authorizer = mock(AddressBasedAuthorizer.class);
        when(authorizer.getRequiredAuthorizedKeys())
                .thenReturn(2);
        when(authorizer.isAuthorized(any(TxSender.class)))
                .thenReturn(true);
        Coin expectedFee = Coin.CENT;

        FeePerKbElection election = new FeePerKbElection(authorizer);

        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getFeePerKb(), nullValue());

        election.vote(new TxSender(new byte[] {0x01}), expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getFeePerKb(), is(expectedFee));

        election.vote(new TxSender(new byte[] {0x02}), expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(true));
        Assert.assertThat(election.getWinner().get(), is(expectedFee));
        Assert.assertThat(election.getVotes().size(), is(2));
    }

    @Test
    public void voteDifferentAndThenWin() {
        AddressBasedAuthorizer authorizer = mock(AddressBasedAuthorizer.class);
        when(authorizer.getRequiredAuthorizedKeys())
                .thenReturn(2);
        when(authorizer.isAuthorized(any(TxSender.class)))
                .thenReturn(true);
        Coin expectedFee = Coin.MILLICOIN;
        Coin unexpectedFee = Coin.CENT;

        FeePerKbElection election = new FeePerKbElection(authorizer);

        Assert.assertThat(election.getWinner().isPresent(), is(false));

        election.vote(new TxSender(new byte[] {0x01}), expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getFeePerKb(), is(expectedFee));

        election.vote(new TxSender(new byte[] {0x02}), unexpectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getFeePerKb(), is(unexpectedFee));

        election.vote(new TxSender(new byte[] {0x03}), expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getFeePerKb(), is(expectedFee));

        election.vote(new TxSender(new byte[] {0x04}), expectedFee);
        Assert.assertThat(election.getWinner().isPresent(), is(true));
        Assert.assertThat(election.getWinner().get(), is(expectedFee));
        Assert.assertThat(election.getFeePerKb(), is(expectedFee));
        Assert.assertThat(election.getVotes().size(), is(2));
    }

    @Test
    public void clearVotes() {
        AddressBasedAuthorizer authorizer = mock(AddressBasedAuthorizer.class);
        when(authorizer.getRequiredAuthorizedKeys())
                .thenReturn(2);
        Coin expectedFee = Coin.FIFTY_COINS;
        HashSet<TxSender> votes = new HashSet<>(Arrays.asList(
                new TxSender(new byte[] {0x01}),
                new TxSender(new byte[] {0x02})
        ));

        FeePerKbElection election = new FeePerKbElection(authorizer, votes, expectedFee);
        election.clear();

        Assert.assertThat(election.getWinner().isPresent(), is(false));
        Assert.assertThat(election.getVotes().size(), is(0));
        Assert.assertThat(election.getFeePerKb(), nullValue());
    }
}