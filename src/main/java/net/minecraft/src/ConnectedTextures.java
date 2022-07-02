package net.minecraft.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ConnectedTextures
{
    private static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
    private static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
    private static boolean multipass = false;
    private static boolean defaultGlassTexture = false;
    private static final int BOTTOM = 0;
    private static final int TOP = 1;
    private static final int EAST = 2;
    private static final int WEST = 3;
    private static final int NORTH = 4;
    private static final int SOUTH = 5;
    private static final String[] propSuffixes = new String[] {"", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private static final int[] ctmIndexes = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 0, 0, 0, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 0, 0, 0, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 0, 0, 0, 0, 0};

    public static Icon getConnectedTexture(IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon)
    {
        if (blockAccess == null)
        {
            return icon;
        }
        else
        {
            Icon newIcon = getConnectedTextureSingle(blockAccess, block, x, y, z, side, icon, true);

            if (!multipass)
            {
                return newIcon;
            }
            else if (newIcon == icon)
            {
                return newIcon;
            }
            else
            {
                Icon mpIcon = newIcon;

                for (int i = 0; i < 3; ++i)
                {
                    Icon newMpIcon = getConnectedTextureSingle(blockAccess, block, x, y, z, side, mpIcon, false);

                    if (newMpIcon == mpIcon)
                    {
                        break;
                    }

                    mpIcon = newMpIcon;
                }

                return mpIcon;
            }
        }
    }

    public static Icon getConnectedTextureSingle(IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, boolean checkBlocks)
    {
        if (!(icon instanceof TextureAtlasSprite))
        {
            return icon;
        }
        else
        {
            TextureAtlasSprite ts = (TextureAtlasSprite)icon;
            int iconId = ts.getIndexInMap();
            int metadata = -1;

            if (tileProperties != null && Tessellator.instance.defaultTexture && iconId >= 0 && iconId < tileProperties.length)
            {
                ConnectedProperties[] blockId = tileProperties[iconId];

                if (blockId != null)
                {
                    if (metadata < 0)
                    {
                        metadata = blockAccess.getBlockMetadata(x, y, z);
                    }

                    Icon cps = getConnectedTexture(blockId, blockAccess, block, x, y, z, side, ts, metadata);

                    if (cps != null)
                    {
                        return cps;
                    }
                }
            }

            if (blockProperties != null && checkBlocks)
            {
                int blockId1 = block.blockID;

                if (blockId1 >= 0 && blockId1 < blockProperties.length)
                {
                    ConnectedProperties[] cps1 = blockProperties[blockId1];

                    if (cps1 != null)
                    {
                        if (metadata < 0)
                        {
                            metadata = blockAccess.getBlockMetadata(x, y, z);
                        }

                        Icon newIcon = getConnectedTexture(cps1, blockAccess, block, x, y, z, side, ts, metadata);

                        if (newIcon != null)
                        {
                            return newIcon;
                        }
                    }
                }
            }

            return icon;
        }
    }

    public static ConnectedProperties getConnectedProperties(IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon)
    {
        if (blockAccess == null)
        {
            return null;
        }
        else if (!(icon instanceof TextureAtlasSprite))
        {
            return null;
        }
        else
        {
            TextureAtlasSprite ts = (TextureAtlasSprite)icon;
            int iconId = ts.getIndexInMap();
            int metadata = -1;

            if (tileProperties != null && Tessellator.instance.defaultTexture && iconId >= 0 && iconId < tileProperties.length)
            {
                ConnectedProperties[] blockId = tileProperties[iconId];

                if (blockId != null)
                {
                    if (metadata < 0)
                    {
                        metadata = blockAccess.getBlockMetadata(x, y, z);
                    }

                    ConnectedProperties cps = getConnectedProperties(blockId, blockAccess, block, x, y, z, side, ts, metadata);

                    if (cps != null)
                    {
                        return cps;
                    }
                }
            }

            if (blockProperties != null)
            {
                int blockId1 = block.blockID;

                if (blockId1 >= 0 && blockId1 < blockProperties.length)
                {
                    ConnectedProperties[] cps1 = blockProperties[blockId1];

                    if (cps1 != null)
                    {
                        if (metadata < 0)
                        {
                            metadata = blockAccess.getBlockMetadata(x, y, z);
                        }

                        ConnectedProperties cp = getConnectedProperties(cps1, blockAccess, block, x, y, z, side, ts, metadata);

                        if (cp != null)
                        {
                            return cp;
                        }
                    }
                }
            }

            return null;
        }
    }

    private static Icon getConnectedTexture(ConnectedProperties[] cps, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        for (int i = 0; i < cps.length; ++i)
        {
            ConnectedProperties cp = cps[i];

            if (cp != null)
            {
                Icon newIcon = getConnectedTexture(cp, blockAccess, block, x, y, z, side, icon, metadata);

                if (newIcon != null)
                {
                    return newIcon;
                }
            }
        }

        return null;
    }

    private static ConnectedProperties getConnectedProperties(ConnectedProperties[] cps, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        for (int i = 0; i < cps.length; ++i)
        {
            ConnectedProperties cp = cps[i];

            if (cp != null)
            {
                Icon newIcon = getConnectedTexture(cp, blockAccess, block, x, y, z, side, icon, metadata);

                if (newIcon != null)
                {
                    return cp;
                }
            }
        }

        return null;
    }

    private static Icon getConnectedTexture(ConnectedProperties cp, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        if (y >= cp.minHeight && y <= cp.maxHeight)
        {
            if (cp.biomes != null)
            {
                BiomeGenBase blockWood = blockAccess.getBiomeGenForCoords(x, z);
                boolean checkMetadata = false;

                for (int mds = 0; mds < cp.biomes.length; ++mds)
                {
                    BiomeGenBase metadataFound = cp.biomes[mds];

                    if (blockWood == metadataFound)
                    {
                        checkMetadata = true;
                        break;
                    }
                }

                if (!checkMetadata)
                {
                    return null;
                }
            }

            boolean var14 = block instanceof BlockLog;
            int var15;

            if (side >= 0 && cp.faces != 63)
            {
                var15 = side;

                if (var14)
                {
                    var15 = fixWoodSide(blockAccess, x, y, z, side, metadata);
                }

                if ((1 << var15 & cp.faces) == 0)
                {
                    return null;
                }
            }

            var15 = metadata;

            if (var14)
            {
                var15 = metadata & 3;
            }

            if (cp.metadatas != null)
            {
                int[] var16 = cp.metadatas;
                boolean var17 = false;

                for (int i = 0; i < var16.length; ++i)
                {
                    if (var16[i] == var15)
                    {
                        var17 = true;
                        break;
                    }
                }

                if (!var17)
                {
                    return null;
                }
            }

            switch (cp.method)
            {
                case 1:
                    return getConnectedTextureCtm(cp, blockAccess, block, x, y, z, side, icon, metadata);

                case 2:
                    return getConnectedTextureHorizontal(cp, blockAccess, block, x, y, z, side, icon, metadata);

                case 3:
                    return getConnectedTextureTop(cp, blockAccess, block, x, y, z, side, icon, metadata);

                case 4:
                    return getConnectedTextureRandom(cp, x, y, z, side);

                case 5:
                    return getConnectedTextureRepeat(cp, x, y, z, side);

                case 6:
                    return getConnectedTextureVertical(cp, blockAccess, block, x, y, z, side, icon, metadata);

                case 7:
                    return getConnectedTextureFixed(cp);

                default:
                    return null;
            }
        }
        else
        {
            return null;
        }
    }

    private static int fixWoodSide(IBlockAccess blockAccess, int x, int y, int z, int side, int metadata)
    {
        int orient = (metadata & 12) >> 2;

        switch (orient)
        {
            case 0:
                return side;

            case 1:
                switch (side)
                {
                    case 0:
                        return 4;

                    case 1:
                        return 5;

                    case 2:
                    case 3:
                    default:
                        return side;

                    case 4:
                        return 1;

                    case 5:
                        return 0;
                }

            case 2:
                switch (side)
                {
                    case 0:
                        return 2;

                    case 1:
                        return 3;

                    case 2:
                        return 1;

                    case 3:
                        return 0;

                    default:
                        return side;
                }

            case 3:
                return 2;

            default:
                return side;
        }
    }

    private static Icon getConnectedTextureRandom(ConnectedProperties cp, int x, int y, int z, int side)
    {
        if (cp.tileIcons.length == 1)
        {
            return cp.tileIcons[0];
        }
        else
        {
            int face = side / cp.symmetry * cp.symmetry;
            int rand = Config.getRandom(x, y, z, face) & Integer.MAX_VALUE;
            int index = 0;

            if (cp.weights == null)
            {
                index = rand % cp.tileIcons.length;
            }
            else
            {
                int randWeight = rand % cp.sumAllWeights;
                int[] sumWeights = cp.sumWeights;

                for (int i = 0; i < sumWeights.length; ++i)
                {
                    if (randWeight < sumWeights[i])
                    {
                        index = i;
                        break;
                    }
                }
            }

            return cp.tileIcons[index];
        }
    }

    private static Icon getConnectedTextureFixed(ConnectedProperties cp)
    {
        return cp.tileIcons[0];
    }

    private static Icon getConnectedTextureRepeat(ConnectedProperties cp, int x, int y, int z, int side)
    {
        if (cp.tileIcons.length == 1)
        {
            return cp.tileIcons[0];
        }
        else
        {
            int nx = 0;
            int ny = 0;

            switch (side)
            {
                case 0:
                    nx = x;
                    ny = z;
                    break;

                case 1:
                    nx = x;
                    ny = z;
                    break;

                case 2:
                    nx = -x - 1;
                    ny = -y;
                    break;

                case 3:
                    nx = x;
                    ny = -y;
                    break;

                case 4:
                    nx = z;
                    ny = -y;
                    break;

                case 5:
                    nx = -z - 1;
                    ny = -y;
            }

            nx %= cp.width;
            ny %= cp.height;

            if (nx < 0)
            {
                nx += cp.width;
            }

            if (ny < 0)
            {
                ny += cp.height;
            }

            int index = ny * cp.width + nx;
            return cp.tileIcons[index];
        }
    }

    private static Icon getConnectedTextureCtm(ConnectedProperties cp, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        boolean[] borders = new boolean[6];

        switch (side)
        {
            case 0:
            case 1:
                borders[0] = isNeighbour(cp, blockAccess, block, x - 1, y, z, side, icon, metadata);
                borders[1] = isNeighbour(cp, blockAccess, block, x + 1, y, z, side, icon, metadata);
                borders[2] = isNeighbour(cp, blockAccess, block, x, y, z + 1, side, icon, metadata);
                borders[3] = isNeighbour(cp, blockAccess, block, x, y, z - 1, side, icon, metadata);
                break;

            case 2:
                borders[0] = isNeighbour(cp, blockAccess, block, x + 1, y, z, side, icon, metadata);
                borders[1] = isNeighbour(cp, blockAccess, block, x - 1, y, z, side, icon, metadata);
                borders[2] = isNeighbour(cp, blockAccess, block, x, y - 1, z, side, icon, metadata);
                borders[3] = isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata);
                break;

            case 3:
                borders[0] = isNeighbour(cp, blockAccess, block, x - 1, y, z, side, icon, metadata);
                borders[1] = isNeighbour(cp, blockAccess, block, x + 1, y, z, side, icon, metadata);
                borders[2] = isNeighbour(cp, blockAccess, block, x, y - 1, z, side, icon, metadata);
                borders[3] = isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata);
                break;

            case 4:
                borders[0] = isNeighbour(cp, blockAccess, block, x, y, z - 1, side, icon, metadata);
                borders[1] = isNeighbour(cp, blockAccess, block, x, y, z + 1, side, icon, metadata);
                borders[2] = isNeighbour(cp, blockAccess, block, x, y - 1, z, side, icon, metadata);
                borders[3] = isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata);
                break;

            case 5:
                borders[0] = isNeighbour(cp, blockAccess, block, x, y, z + 1, side, icon, metadata);
                borders[1] = isNeighbour(cp, blockAccess, block, x, y, z - 1, side, icon, metadata);
                borders[2] = isNeighbour(cp, blockAccess, block, x, y - 1, z, side, icon, metadata);
                borders[3] = isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata);
        }

        byte index = 0;

        if (borders[0] & !borders[1] & !borders[2] & !borders[3])
        {
            index = 3;
        }
        else if (!borders[0] & borders[1] & !borders[2] & !borders[3])
        {
            index = 1;
        }
        else if (!borders[0] & !borders[1] & borders[2] & !borders[3])
        {
            index = 12;
        }
        else if (!borders[0] & !borders[1] & !borders[2] & borders[3])
        {
            index = 36;
        }
        else if (borders[0] & borders[1] & !borders[2] & !borders[3])
        {
            index = 2;
        }
        else if (!borders[0] & !borders[1] & borders[2] & borders[3])
        {
            index = 24;
        }
        else if (borders[0] & !borders[1] & borders[2] & !borders[3])
        {
            index = 15;
        }
        else if (borders[0] & !borders[1] & !borders[2] & borders[3])
        {
            index = 39;
        }
        else if (!borders[0] & borders[1] & borders[2] & !borders[3])
        {
            index = 13;
        }
        else if (!borders[0] & borders[1] & !borders[2] & borders[3])
        {
            index = 37;
        }
        else if (!borders[0] & borders[1] & borders[2] & borders[3])
        {
            index = 25;
        }
        else if (borders[0] & !borders[1] & borders[2] & borders[3])
        {
            index = 27;
        }
        else if (borders[0] & borders[1] & !borders[2] & borders[3])
        {
            index = 38;
        }
        else if (borders[0] & borders[1] & borders[2] & !borders[3])
        {
            index = 14;
        }
        else if (borders[0] & borders[1] & borders[2] & borders[3])
        {
            index = 26;
        }

        if (!Config.isConnectedTexturesFancy())
        {
            return cp.tileIcons[index];
        }
        else
        {
            boolean[] edges = new boolean[6];

            switch (side)
            {
                case 0:
                case 1:
                    edges[0] = !isNeighbour(cp, blockAccess, block, x + 1, y, z + 1, side, icon, metadata);
                    edges[1] = !isNeighbour(cp, blockAccess, block, x - 1, y, z + 1, side, icon, metadata);
                    edges[2] = !isNeighbour(cp, blockAccess, block, x + 1, y, z - 1, side, icon, metadata);
                    edges[3] = !isNeighbour(cp, blockAccess, block, x - 1, y, z - 1, side, icon, metadata);
                    break;

                case 2:
                    edges[0] = !isNeighbour(cp, blockAccess, block, x - 1, y - 1, z, side, icon, metadata);
                    edges[1] = !isNeighbour(cp, blockAccess, block, x + 1, y - 1, z, side, icon, metadata);
                    edges[2] = !isNeighbour(cp, blockAccess, block, x - 1, y + 1, z, side, icon, metadata);
                    edges[3] = !isNeighbour(cp, blockAccess, block, x + 1, y + 1, z, side, icon, metadata);
                    break;

                case 3:
                    edges[0] = !isNeighbour(cp, blockAccess, block, x + 1, y - 1, z, side, icon, metadata);
                    edges[1] = !isNeighbour(cp, blockAccess, block, x - 1, y - 1, z, side, icon, metadata);
                    edges[2] = !isNeighbour(cp, blockAccess, block, x + 1, y + 1, z, side, icon, metadata);
                    edges[3] = !isNeighbour(cp, blockAccess, block, x - 1, y + 1, z, side, icon, metadata);
                    break;

                case 4:
                    edges[0] = !isNeighbour(cp, blockAccess, block, x, y - 1, z + 1, side, icon, metadata);
                    edges[1] = !isNeighbour(cp, blockAccess, block, x, y - 1, z - 1, side, icon, metadata);
                    edges[2] = !isNeighbour(cp, blockAccess, block, x, y + 1, z + 1, side, icon, metadata);
                    edges[3] = !isNeighbour(cp, blockAccess, block, x, y + 1, z - 1, side, icon, metadata);
                    break;

                case 5:
                    edges[0] = !isNeighbour(cp, blockAccess, block, x, y - 1, z - 1, side, icon, metadata);
                    edges[1] = !isNeighbour(cp, blockAccess, block, x, y - 1, z + 1, side, icon, metadata);
                    edges[2] = !isNeighbour(cp, blockAccess, block, x, y + 1, z - 1, side, icon, metadata);
                    edges[3] = !isNeighbour(cp, blockAccess, block, x, y + 1, z + 1, side, icon, metadata);
            }

            if (index == 13 && edges[0])
            {
                index = 4;
            }

            if (index == 15 && edges[1])
            {
                index = 5;
            }

            if (index == 37 && edges[2])
            {
                index = 16;
            }

            if (index == 39 && edges[3])
            {
                index = 17;
            }

            if (index == 14 && edges[0] && edges[1])
            {
                index = 7;
            }

            if (index == 25 && edges[0] && edges[2])
            {
                index = 6;
            }

            if (index == 27 && edges[3] && edges[1])
            {
                index = 19;
            }

            if (index == 38 && edges[3] && edges[2])
            {
                index = 18;
            }

            if (index == 14 && !edges[0] && edges[1])
            {
                index = 31;
            }

            if (index == 25 && edges[0] && !edges[2])
            {
                index = 30;
            }

            if (index == 27 && !edges[3] && edges[1])
            {
                index = 41;
            }

            if (index == 38 && edges[3] && !edges[2])
            {
                index = 40;
            }

            if (index == 14 && edges[0] && !edges[1])
            {
                index = 29;
            }

            if (index == 25 && !edges[0] && edges[2])
            {
                index = 28;
            }

            if (index == 27 && edges[3] && !edges[1])
            {
                index = 43;
            }

            if (index == 38 && !edges[3] && edges[2])
            {
                index = 42;
            }

            if (index == 26 && edges[0] && edges[1] && edges[2] && edges[3])
            {
                index = 46;
            }

            if (index == 26 && !edges[0] && edges[1] && edges[2] && edges[3])
            {
                index = 9;
            }

            if (index == 26 && edges[0] && !edges[1] && edges[2] && edges[3])
            {
                index = 21;
            }

            if (index == 26 && edges[0] && edges[1] && !edges[2] && edges[3])
            {
                index = 8;
            }

            if (index == 26 && edges[0] && edges[1] && edges[2] && !edges[3])
            {
                index = 20;
            }

            if (index == 26 && edges[0] && edges[1] && !edges[2] && !edges[3])
            {
                index = 11;
            }

            if (index == 26 && !edges[0] && !edges[1] && edges[2] && edges[3])
            {
                index = 22;
            }

            if (index == 26 && !edges[0] && edges[1] && !edges[2] && edges[3])
            {
                index = 23;
            }

            if (index == 26 && edges[0] && !edges[1] && edges[2] && !edges[3])
            {
                index = 10;
            }

            if (index == 26 && edges[0] && !edges[1] && !edges[2] && edges[3])
            {
                index = 34;
            }

            if (index == 26 && !edges[0] && edges[1] && edges[2] && !edges[3])
            {
                index = 35;
            }

            if (index == 26 && edges[0] && !edges[1] && !edges[2] && !edges[3])
            {
                index = 32;
            }

            if (index == 26 && !edges[0] && edges[1] && !edges[2] && !edges[3])
            {
                index = 33;
            }

            if (index == 26 && !edges[0] && !edges[1] && edges[2] && !edges[3])
            {
                index = 44;
            }

            if (index == 26 && !edges[0] && !edges[1] && !edges[2] && edges[3])
            {
                index = 45;
            }

            return cp.tileIcons[index];
        }
    }

    private static boolean isNeighbour(ConnectedProperties cp, IBlockAccess iblockaccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        int blockId = iblockaccess.getBlockId(x, y, z);
        Block neighbourBlock;

        if (cp.connect == 2)
        {
            neighbourBlock = Block.blocksList[blockId];

            if (neighbourBlock == null)
            {
                return false;
            }
            else
            {
                Icon neighbourIcon;

                if (side >= 0)
                {
                    neighbourIcon = neighbourBlock.getBlockTexture(iblockaccess, x, y, z, side);
                }
                else
                {
                    neighbourIcon = neighbourBlock.getBlockTexture(iblockaccess, x, y, z, 1);
                }

                return neighbourIcon == icon;
            }
        }
        else if (cp.connect == 3)
        {
            neighbourBlock = Block.blocksList[blockId];
            return neighbourBlock == null ? false : neighbourBlock.blockMaterial == block.blockMaterial;
        }
        else
        {
            return blockId == block.blockID && iblockaccess.getBlockMetadata(x, y, z) == metadata;
        }
    }

    private static Icon getConnectedTextureHorizontal(ConnectedProperties cp, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        if (side != 0 && side != 1)
        {
            boolean left = false;
            boolean right = false;

            switch (side)
            {
                case 2:
                    left = isNeighbour(cp, blockAccess, block, x + 1, y, z, side, icon, metadata);
                    right = isNeighbour(cp, blockAccess, block, x - 1, y, z, side, icon, metadata);
                    break;

                case 3:
                    left = isNeighbour(cp, blockAccess, block, x - 1, y, z, side, icon, metadata);
                    right = isNeighbour(cp, blockAccess, block, x + 1, y, z, side, icon, metadata);
                    break;

                case 4:
                    left = isNeighbour(cp, blockAccess, block, x, y, z - 1, side, icon, metadata);
                    right = isNeighbour(cp, blockAccess, block, x, y, z + 1, side, icon, metadata);
                    break;

                case 5:
                    left = isNeighbour(cp, blockAccess, block, x, y, z + 1, side, icon, metadata);
                    right = isNeighbour(cp, blockAccess, block, x, y, z - 1, side, icon, metadata);
            }

            boolean index = true;
            byte index1;

            if (left)
            {
                if (right)
                {
                    index1 = 1;
                }
                else
                {
                    index1 = 2;
                }
            }
            else if (right)
            {
                index1 = 0;
            }
            else
            {
                index1 = 3;
            }

            return cp.tileIcons[index1];
        }
        else
        {
            return null;
        }
    }

    private static Icon getConnectedTextureVertical(ConnectedProperties cp, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        if (side != 0 && side != 1)
        {
            boolean bottom = isNeighbour(cp, blockAccess, block, x, y - 1, z, side, icon, metadata);
            boolean top = isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata);
            boolean index = true;
            byte index1;

            if (bottom)
            {
                if (top)
                {
                    index1 = 1;
                }
                else
                {
                    index1 = 2;
                }
            }
            else if (top)
            {
                index1 = 0;
            }
            else
            {
                index1 = 3;
            }

            return cp.tileIcons[index1];
        }
        else
        {
            return null;
        }
    }

    private static Icon getConnectedTextureTop(ConnectedProperties cp, IBlockAccess blockAccess, Block block, int x, int y, int z, int side, Icon icon, int metadata)
    {
        return side != 0 && side != 1 ? (isNeighbour(cp, blockAccess, block, x, y + 1, z, side, icon, metadata) ? cp.tileIcons[0] : null) : null;
    }

    public static boolean isConnectedGlassPanes()
    {
        return Config.isConnectedTextures() && defaultGlassTexture;
    }

    public static void updateIcons(TextureMap textureMap)
    {
        blockProperties = (ConnectedProperties[][])null;
        tileProperties = (ConnectedProperties[][])null;
        defaultGlassTexture = false;
        ResourcePack rp = Config.getResourcePack();
        ResourceLocation loc = new ResourceLocation("textures/blocks/glass.png");
        boolean tpHasGlass = rp.resourceExists(loc);
        defaultGlassTexture = !tpHasGlass;
        String[] names = collectFiles(rp, "mcpatcher/ctm/", ".properties");
        Arrays.sort(names);
        ArrayList tileList = new ArrayList();
        ArrayList blockList = new ArrayList();

        for (int i = 0; i < names.length; ++i)
        {
            String name = names[i];
            Config.dbg("ConnectedTextures: " + name);

            try
            {
                ResourceLocation e = new ResourceLocation(name);
                InputStream in = rp.getInputStream(e);

                if (in == null)
                {
                    Config.warn("ConnectedTextures file not found: " + name);
                }
                else
                {
                    Properties props = new Properties();
                    props.load(in);
                    ConnectedProperties cp = new ConnectedProperties(props, name);

                    if (cp.isValid(name))
                    {
                        cp.updateIcons(textureMap);
                        addToTileList(cp, tileList);
                        addToBlockList(cp, blockList);
                    }
                }
            }
            catch (FileNotFoundException var13)
            {
                Config.warn("ConnectedTextures file not found: " + name);
            }
            catch (IOException var14)
            {
                var14.printStackTrace();
            }
        }

        blockProperties = propertyListToArray(blockList);
        tileProperties = propertyListToArray(tileList);
        multipass = detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    private static boolean detectMultipass()
    {
        ArrayList propList = new ArrayList();
        int props;
        ConnectedProperties[] matchIconSet;

        for (props = 0; props < tileProperties.length; ++props)
        {
            matchIconSet = tileProperties[props];

            if (matchIconSet != null)
            {
                propList.addAll(Arrays.asList(matchIconSet));
            }
        }

        for (props = 0; props < blockProperties.length; ++props)
        {
            matchIconSet = blockProperties[props];

            if (matchIconSet != null)
            {
                propList.addAll(Arrays.asList(matchIconSet));
            }
        }

        ConnectedProperties[] var6 = (ConnectedProperties[])((ConnectedProperties[])propList.toArray(new ConnectedProperties[propList.size()]));
        HashSet var7 = new HashSet();
        HashSet tileIconSet = new HashSet();

        for (int i = 0; i < var6.length; ++i)
        {
            ConnectedProperties cp = var6[i];

            if (cp.matchTileIcons != null)
            {
                var7.addAll(Arrays.asList(cp.matchTileIcons));
            }

            if (cp.tileIcons != null)
            {
                tileIconSet.addAll(Arrays.asList(cp.tileIcons));
            }
        }

        var7.retainAll(tileIconSet);
        return !var7.isEmpty();
    }

    private static ConnectedProperties[][] propertyListToArray(List list)
    {
        ConnectedProperties[][] propArr = new ConnectedProperties[list.size()][];

        for (int i = 0; i < list.size(); ++i)
        {
            List subList = (List)list.get(i);

            if (subList != null)
            {
                ConnectedProperties[] subArr = (ConnectedProperties[])((ConnectedProperties[])subList.toArray(new ConnectedProperties[subList.size()]));
                propArr[i] = subArr;
            }
        }

        return propArr;
    }

    private static void addToTileList(ConnectedProperties cp, List tileList)
    {
        if (cp.matchTileIcons != null)
        {
            for (int i = 0; i < cp.matchTileIcons.length; ++i)
            {
                Icon icon = cp.matchTileIcons[i];

                if (!(icon instanceof TextureAtlasSprite))
                {
                    Config.warn("Icon is not TextureAtlasSprite: " + icon + ", name: " + icon.getIconName());
                }
                else
                {
                    TextureAtlasSprite ts = (TextureAtlasSprite)icon;
                    int tileId = ts.getIndexInMap();

                    if (tileId < 0)
                    {
                        Config.warn("Invalid tile ID: " + tileId + ", icon: " + ts.getIconName());
                    }
                    else
                    {
                        addToList(cp, tileList, tileId);
                    }
                }
            }
        }
    }

    private static void addToBlockList(ConnectedProperties cp, List blockList)
    {
        if (cp.matchBlocks != null)
        {
            for (int i = 0; i < cp.matchBlocks.length; ++i)
            {
                int blockId = cp.matchBlocks[i];

                if (blockId < 0)
                {
                    Config.warn("Invalid block ID: " + blockId);
                }
                else
                {
                    addToList(cp, blockList, blockId);
                }
            }
        }
    }

    private static void addToList(ConnectedProperties cp, List list, int id)
    {
        while (id >= list.size())
        {
            list.add((Object)null);
        }

        Object subList = (List)list.get(id);

        if (subList == null)
        {
            subList = new ArrayList();
            list.set(id, subList);
        }

        ((List)subList).add(cp);
    }

    private static String[] collectFiles(ResourcePack rp, String prefix, String suffix)
    {
        if (rp instanceof DefaultResourcePack)
        {
            return collectFilesDefault(rp);
        }
        else if (!(rp instanceof AbstractResourcePack))
        {
            return new String[0];
        }
        else
        {
            AbstractResourcePack arp = (AbstractResourcePack)rp;
            File tpFile = arp.resourcePackFile;
            return tpFile == null ? new String[0] : (tpFile.isDirectory() ? collectFilesFolder(tpFile, "", prefix, suffix) : (tpFile.isFile() ? collectFilesZIP(tpFile, prefix, suffix) : new String[0]));
        }
    }

    private static String[] collectFilesDefault(ResourcePack rp)
    {
        ArrayList list = new ArrayList();
        String[] names = new String[] {"mcpatcher/ctm/default/bookshelf.properties", "mcpatcher/ctm/default/glass.properties", "mcpatcher/ctm/default/glasspane.properties", "mcpatcher/ctm/default/sandstone.properties"};

        for (int nameArr = 0; nameArr < names.length; ++nameArr)
        {
            String name = names[nameArr];
            ResourceLocation loc = new ResourceLocation(name);

            if (rp.resourceExists(loc))
            {
                list.add(name);
            }
        }

        String[] var6 = (String[])((String[])list.toArray(new String[list.size()]));
        return var6;
    }

    private static String[] collectFilesFolder(File tpFile, String basePath, String prefix, String suffix)
    {
        ArrayList list = new ArrayList();
        String prefixAssets = "assets/minecraft/";
        File[] files = tpFile.listFiles();

        if (files == null)
        {
            return new String[0];
        }
        else
        {
            for (int names = 0; names < files.length; ++names)
            {
                File file = files[names];
                String dirPath;

                if (file.isFile())
                {
                    dirPath = basePath + file.getName();

                    if (dirPath.startsWith(prefixAssets))
                    {
                        dirPath = dirPath.substring(prefixAssets.length());

                        if (dirPath.startsWith(prefix) && dirPath.endsWith(suffix))
                        {
                            list.add(dirPath);
                        }
                    }
                }
                else if (file.isDirectory())
                {
                    dirPath = basePath + file.getName() + "/";
                    String[] names1 = collectFilesFolder(file, dirPath, prefix, suffix);

                    for (int n = 0; n < names1.length; ++n)
                    {
                        String name = names1[n];
                        list.add(name);
                    }
                }
            }

            String[] var13 = (String[])((String[])list.toArray(new String[list.size()]));
            return var13;
        }
    }

    private static String[] collectFilesZIP(File tpFile, String prefix, String suffix)
    {
        ArrayList list = new ArrayList();
        String prefixAssets = "assets/minecraft/";

        try
        {
            ZipFile e = new ZipFile(tpFile);
            Enumeration en = e.entries();

            while (en.hasMoreElements())
            {
                ZipEntry names = (ZipEntry)en.nextElement();
                String name = names.getName();

                if (name.startsWith(prefixAssets))
                {
                    name = name.substring(prefixAssets.length());

                    if (name.startsWith(prefix) && name.endsWith(suffix))
                    {
                        list.add(name);
                    }
                }
            }

            e.close();
            String[] names1 = (String[])((String[])list.toArray(new String[list.size()]));
            return names1;
        }
        catch (IOException var9)
        {
            var9.printStackTrace();
            return new String[0];
        }
    }

    public static int getPaneTextureIndex(boolean linkP, boolean linkN, boolean linkYp, boolean linkYn)
    {
        return linkN && linkP ? (linkYp ? (linkYn ? 34 : 50) : (linkYn ? 18 : 2)) : (linkN && !linkP ? (linkYp ? (linkYn ? 35 : 51) : (linkYn ? 19 : 3)) : (!linkN && linkP ? (linkYp ? (linkYn ? 33 : 49) : (linkYn ? 17 : 1)) : (linkYp ? (linkYn ? 32 : 48) : (linkYn ? 16 : 0))));
    }

    public static int getReversePaneTextureIndex(int texNum)
    {
        int col = texNum % 16;
        return col == 1 ? texNum + 2 : (col == 3 ? texNum - 2 : texNum);
    }

    public static Icon getCtmTexture(ConnectedProperties cp, int ctmIndex, Icon icon)
    {
        if (cp.method != 1)
        {
            return icon;
        }
        else if (ctmIndex >= 0 && ctmIndex < ctmIndexes.length)
        {
            int index = ctmIndexes[ctmIndex];
            Icon[] ctmIcons = cp.tileIcons;
            return index >= 0 && index < ctmIcons.length ? ctmIcons[index] : icon;
        }
        else
        {
            return icon;
        }
    }
}
