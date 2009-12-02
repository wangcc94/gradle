/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.cache.btree;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class BlockPayload {
    private Block block;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPointer getPos() {
        return getBlock().getPos();
    }

    public BlockPointer getNextPos() {
        return getBlock().getNextPos();
    }

    protected abstract int getSize();

    protected abstract int getType();

    protected abstract void read(DataInputStream inputStream) throws Exception;

    protected abstract void write(DataOutputStream outputStream) throws Exception;

    protected RuntimeException blockCorruptedException() {
        return getBlock().blockCorruptedException();
    }
}
