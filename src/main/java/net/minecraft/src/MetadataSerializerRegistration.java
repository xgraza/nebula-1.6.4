package net.minecraft.src;

class MetadataSerializerRegistration
{
    final MetadataSectionSerializer field_110502_a;
    final Class field_110500_b;

    final MetadataSerializer field_110501_c;

    private MetadataSerializerRegistration(MetadataSerializer par1MetadataSerializer, MetadataSectionSerializer par2MetadataSectionSerializer, Class par3Class)
    {
        this.field_110501_c = par1MetadataSerializer;
        this.field_110502_a = par2MetadataSectionSerializer;
        this.field_110500_b = par3Class;
    }

    MetadataSerializerRegistration(MetadataSerializer par1MetadataSerializer, MetadataSectionSerializer par2MetadataSectionSerializer, Class par3Class, MetadataSerializerEmptyAnon par4MetadataSerializerEmptyAnon)
    {
        this(par1MetadataSerializer, par2MetadataSectionSerializer, par3Class);
    }
}
