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

package co.rsk.trie;

/**
 * Created by ajlopez on 29/03/2017.
 */
public interface Trie {
    int getArity();

    byte[] getHash();

    byte[] get(byte[] key);

    PartialMerkleTree getPartialMerkleTree(byte[] key);

    byte[] get(String key);

    Trie put(byte[] key, byte[] value);

    Trie put(String key, byte[] value);

    Trie delete(byte[] key);

    Trie delete(String key);

    byte[] toMessage();

    void save();

    int trieSize();

    Trie cloneTrie();

    Trie cloneTrie(byte[] newValue);

    void removeNode(int position);

    void removeValue();

    void setHash(int n, byte[] hash);

    Trie getSnapshotTo(byte[] hash);

    byte[] serialize();

    boolean hasStore();

    boolean hasLongValue();

    byte[] getValueHash();

    byte[] getValue();
}
