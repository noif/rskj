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

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Representation of the state of the current
 * fee per kb election, with known and
 * authorized electors.
 */
public class FeePerKbElection {
    private final AddressBasedAuthorizer authorizer;
    private final Set<TxSender> votes;

    @Nullable
    private Coin feePerKb;

    public FeePerKbElection(AddressBasedAuthorizer authorizer, Set<TxSender> votes, Coin feePerKb) {
        this.authorizer = authorizer;
        this.votes = votes;
        this.feePerKb = feePerKb;
    }

    public FeePerKbElection(AddressBasedAuthorizer authorizer) {
        this(authorizer, new HashSet<>(), null);
    }

    public Set<TxSender> getVotes() {
        return votes;
    }

    public Coin getFeePerKb() {
        return feePerKb;
    }

    public void clear() {
        this.votes.clear();
        this.feePerKb = null;
    }

    /**
     * Register voter's vote
     *
     * @param voter    the voter
     * @param feePerKb the value to vote for
     * @return whether the voting succeeded
     */
    public boolean vote(TxSender voter, Coin feePerKb) {
        Objects.requireNonNull(feePerKb, "Can't vote for a null fee per kb");

        if (!authorizer.isAuthorized(voter)) {
            return false;
        }

        // whenever an authorized elector votes for a different
        // feePerKb the election is restarted.
        if (this.feePerKb == null || !this.feePerKb.equals(feePerKb)) {
            clear();
            this.feePerKb = feePerKb;
        }

        // if the voter already voted add() returns false.
        return votes.add(voter);
    }

    /**
     * Returns the election winner fee per kb, or empty if there's none.
     * @return the winner fee per kb
     */
    public Optional<Coin> getWinner() {
        if (votes.size() >= authorizer.getRequiredAuthorizedKeys()) {
            return Optional.ofNullable(feePerKb);
        }

        return Optional.empty();
    }
}
