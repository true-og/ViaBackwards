/*
 * Copyright (c) 2016 Matsv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package nl.matsv.viabackwards.protocol.protocol1_12to1_11_1.packets;

import nl.matsv.viabackwards.api.rewriters.BlockItemRewriter;
import nl.matsv.viabackwards.protocol.protocol1_12to1_11_1.Protocol1_11_1To1_12;
import nl.matsv.viabackwards.utils.Block;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_12;
import us.myles.ViaVersion.api.remapper.PacketHandler;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.packets.State;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.chunks.Chunk1_9_3_4;
import us.myles.ViaVersion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class BlockItemPackets1_12 extends BlockItemRewriter<Protocol1_11_1To1_12> {
    @Override
    protected void registerPackets(Protocol1_11_1To1_12 protocol) {
          /* Item packets */

        // Set slot packet
        protocol.registerOutgoing(State.PLAY, 0x16, 0x16, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // 0 - Window ID
                map(Type.SHORT); // 1 - Slot ID
                map(Type.ITEM); // 2 - Slot Value

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item stack = wrapper.get(Type.ITEM, 0);
                        wrapper.set(Type.ITEM, 0, handleItemToClient(stack));
                    }
                });
            }
        });

        // Window items packet
        protocol.registerOutgoing(State.PLAY, 0x14, 0x14, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // 0 - Window ID
                map(Type.ITEM_ARRAY); // 1 - Window Values

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item[] stacks = wrapper.get(Type.ITEM_ARRAY, 0);
                        for (int i = 0; i < stacks.length; i++)
                            stacks[i] = handleItemToClient(stacks[i]);
                    }
                });
            }
        });

        // Entity Equipment Packet
        protocol.registerOutgoing(State.PLAY, 0x3E, 0x3C, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // 0 - Entity ID
                map(Type.VAR_INT); // 1 - Slot ID
                map(Type.ITEM); // 2 - Item

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item stack = wrapper.get(Type.ITEM, 0);
                        wrapper.set(Type.ITEM, 0, handleItemToClient(stack));
                    }
                });
            }
        });

        // Plugin message Packet -> Trading
        protocol.registerOutgoing(State.PLAY, 0x18, 0x18, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // 0 - Channel

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.STRING, 0).equalsIgnoreCase("MC|TrList")) {
                            wrapper.passthrough(Type.INT); // Passthrough Window ID

                            int size = wrapper.passthrough(Type.BYTE);
                            for (int i = 0; i < size; i++) {
                                wrapper.write(Type.ITEM, handleItemToClient(wrapper.read(Type.ITEM))); // Input Item
                                wrapper.write(Type.ITEM, handleItemToClient(wrapper.read(Type.ITEM))); // Output Item

                                boolean secondItem = wrapper.passthrough(Type.BOOLEAN); // Has second item
                                if (secondItem)
                                    wrapper.write(Type.ITEM, handleItemToClient(wrapper.read(Type.ITEM))); // Second Item

                                wrapper.passthrough(Type.BOOLEAN); // Trade disabled
                                wrapper.passthrough(Type.INT); // Number of tools uses
                                wrapper.passthrough(Type.INT); // Maximum number of trade uses
                            }
                        }
                    }
                });
            }
        });

        // Click window packet
        protocol.registerIncoming(State.PLAY, 0x08, 0x07, new PacketRemapper() {
                    @Override
                    public void registerMap() {
                        map(Type.UNSIGNED_BYTE); // 0 - Window ID
                        map(Type.SHORT); // 1 - Slot
                        map(Type.BYTE); // 2 - Button
                        map(Type.SHORT); // 3 - Action number
                        map(Type.VAR_INT); // 4 - Mode
                        map(Type.ITEM); // 5 - Clicked Item

                        handler(new PacketHandler() {
                            @Override
                            public void handle(PacketWrapper wrapper) throws Exception {
                                Item item = wrapper.get(Type.ITEM, 0);
                                handleItemToServer(item);
                            }
                        });
                    }
                }
        );

        // Creative Inventory Action
        protocol.registerIncoming(State.PLAY, 0x1B, 0x18, new PacketRemapper() {
                    @Override
                    public void registerMap() {
                        map(Type.SHORT); // 0 - Slot
                        map(Type.ITEM); // 1 - Clicked Item

                        handler(new PacketHandler() {
                            @Override
                            public void handle(PacketWrapper wrapper) throws Exception {
                                Item item = wrapper.get(Type.ITEM, 0);
                                handleItemToServer(item);
                            }
                        });
                    }
                }
        );

        /* Block packets */

        // Chunk packet
        protocol.registerOutgoing(State.PLAY, 0x20, 0x20, new PacketRemapper() {
                    @Override
                    public void registerMap() {
                        handler(new PacketHandler() {
                            @Override
                            public void handle(PacketWrapper wrapper) throws Exception {
                                ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);

                                Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld); // Use the 1.9.4 Chunk type since nothing changed.
                                Chunk1_9_3_4 chunk = (Chunk1_9_3_4) wrapper.passthrough(type);

                                handleChunk(chunk);
                            }
                        });
                    }
                }
        );

        // Block Change Packet
        protocol.registerOutgoing(State.PLAY, 0x0B, 0x0B, new PacketRemapper() {
                    @Override
                    public void registerMap() {
                        map(Type.POSITION); // 0 - Block Position
                        map(Type.VAR_INT); // 1 - Block

                        handler(new PacketHandler() {
                            @Override
                            public void handle(PacketWrapper wrapper) throws Exception {
                                int idx = wrapper.get(Type.VAR_INT, 0);
                                wrapper.set(Type.VAR_INT, 0, handleBlockID(idx));
                            }
                        });
                    }
                }
        );

        // Multi Block Change Packet
        protocol.registerOutgoing(State.PLAY, 0x10, 0x10, new PacketRemapper() {
                    @Override
                    public void registerMap() {
                        map(Type.INT); // 0 - Chunk X
                        map(Type.INT); // 1 - Chunk Z

                        handler(new PacketHandler() {
                            @Override
                            public void handle(PacketWrapper wrapper) throws Exception {
                                int count = wrapper.passthrough(Type.VAR_INT); // Array length

                                for (int i = 0; i < count; i++) {
                                    wrapper.passthrough(Type.UNSIGNED_BYTE); // Horizontal position
                                    wrapper.passthrough(Type.UNSIGNED_BYTE); // Y coords

                                    int id = wrapper.read(Type.VAR_INT); // Block ID
                                    wrapper.write(Type.VAR_INT, handleBlockID(id));
                                }
                            }
                        });
                    }
                }
        );

        // Update Block Entity
        protocol.registerOutgoing(State.PLAY, 0x09, 0x09, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION); // 0 - Position
                map(Type.UNSIGNED_BYTE); // 1 - Action
                map(Type.NBT); // 2 - NBT

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        // Remove bed color
                        if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 11)
                            wrapper.cancel();
                    }
                });
            }
        });

        protocol.getEntityPackets().registerMetaHandler().handle(e -> {
            Metadata data = e.getData();

            if (data.getMetaType().equals(MetaType1_12.Slot)) // Is Item
                data.setValue(handleItemToClient((Item) data.getValue()));

            return data;
        });

        // Client Status
        protocol.registerIncoming(State.PLAY, 0x04, 0x03, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Action ID

                handler(new PacketHandler() {
                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        // Open Inventory
                        if (wrapper.get(Type.VAR_INT, 0) == 2)
                            wrapper.cancel(); // TODO is this replaced by something else?
                    }
                });
            }
        });
    }

    @Override
    protected void registerRewrites() {
        // Concrete -> Stained clay? (Also got a new name Terracota?)
        rewrite(251)
                .repItem(new Item((short) 159, (byte) 1, (short) -1, getNamedTag("1.12 Concrete")))
                .repBlock(new Block(159, -1)); // TODO color provided by name

        // Concrete Powder -> Wool
        rewrite(252)
                .repItem(new Item((short) 35, (byte) 1, (short) -1, getNamedTag("1.12 Concrete Powder")))
                .repBlock(new Block(35, -1)); // TODO color provided by name

        // Knowledge book -> book
        rewrite(453)
                .repItem(new Item((short) 340, (byte) 1, (short) 0, getNamedTag("1.12 Knowledge Book"))); // TODO glow

        // Glazed Terracotta -> Stained Clay
        for (int i = 235; i < 250; i++) {
            rewrite(i).repItem(new Item((short) 159, (byte) 1, (short) (i - 235), getNamedTag("1.12 Glazed Terracotta")))
                    .repBlock(new Block(159, (i - 235))); // TODO color provided by name
        }

    }
}
