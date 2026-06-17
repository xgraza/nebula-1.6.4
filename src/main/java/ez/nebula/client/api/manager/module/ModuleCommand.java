package ez.nebula.client.api.manager.module;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.impl.gui.module.component.module.value.EnumSettingComponent;

import java.awt.Color;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 6/16/26
 */
@CommandManifest
public final class ModuleCommand extends Command
{
    private final Module module;
    private final String name;

    public ModuleCommand(final Module module)
    {
        this.module = module;
        this.name = module.getManifest().name();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void createBuilder(final LiteralArgumentBuilder<CommandSource> literal)
    {
        for (final Setting<?> setting : module.getSettings())
        {
            final LiteralArgumentBuilder<CommandSource> settingLiteral = literal(setting.getName().replace(" ", ""));
            if (Boolean.class.isAssignableFrom(setting.getType()))
            {
                registerBooleanSetting(settingLiteral, (Setting<Boolean>) setting);
            } else if (Number.class.isAssignableFrom(setting.getType()))
            {
                registerNumberSetting(settingLiteral, (NumberSetting<?>) setting);
            } else if (Enum.class.isAssignableFrom(setting.getType()))
            {
                registerEnumSetting(settingLiteral, (EnumSetting<?>) setting);
            } else if (String.class.isAssignableFrom(setting.getType()))
            {
                registerStringSetting(settingLiteral, (Setting<String>) setting);
            } else if (Color.class.isAssignableFrom(setting.getType()))
            {
                registerColorSetting(settingLiteral, (ColorSetting) setting);
            }

            if (Color.class.isAssignableFrom(setting.getType()) || Enum.class.isAssignableFrom(setting.getType()))
            {
                literal.then(settingLiteral);
                continue;
            }

            settingLiteral.executes((ctx) ->
                    ctx.getSource().respond("%s.%s = \"%s\"", name, setting.getName(), setting.getValue()));
            literal.then(settingLiteral);
        }

        literal.then(literal("resetSettings")
                .executes((ctx) ->
                {
                    for (final Setting<?> setting : module.getSettings())
                    {
                        setting.setToDefault();
                    }
                    return ctx.getSource().respond("Reset %s's settings back to default", name);
                }));

        literal.executes((ctx) ->
        {
            final List<Setting<?>> settingList = module.getSettings();
            final StringJoiner joiner = new StringJoiner(", ");
            for (final Setting<?> setting : settingList)
            {
                joiner.add(setting.getName());
            }
            return ctx.getSource().respond("%s has %s settings to configure: %s",
                    name, settingList.size(), joiner.toString());
        });
    }

    private void registerBooleanSetting(final LiteralArgumentBuilder<CommandSource> literal, final Setting<Boolean> setting)
    {
        literal.then(argument("value", BoolArgumentType.bool())
                        .executes((ctx) ->
                        {
                            final boolean value = BoolArgumentType.getBool(ctx, "value");
                            setting.setValue(value);
                            return ctx.getSource().respond("Set %s.%s to %s", name, setting.getName(), value);
                        }));
    }

    @SuppressWarnings("unchecked")
    private void registerNumberSetting(final LiteralArgumentBuilder<CommandSource> literal, final NumberSetting<?> setting)
    {
        literal.then(argument("value", DoubleArgumentType.doubleArg(setting.getMin().doubleValue(), setting.getMax().doubleValue()))
                .executes((ctx) ->
                {
                    final Double value = DoubleArgumentType.getDouble(ctx, "value");
                    if (Double.isNaN(value))
                    {
                        return ctx.getSource().respond("Value cannot be NaN");
                    }
                    final Class<?> type = setting.getType();
                    if (Integer.class.isAssignableFrom(type))
                    {
                        ((NumberSetting<Integer>) setting).setValue(value.intValue());
                    } else if (Float.class.isAssignableFrom(type))
                    {
                        ((NumberSetting<Float>) setting).setValue(value.floatValue());
                    } else if (Long.class.isAssignableFrom(type))
                    {
                        ((NumberSetting<Long>) setting).setValue(value.longValue());
                    } else if (Double.class.isAssignableFrom(type))
                    {
                        ((NumberSetting<Double>) setting).setValue(value);
                    } else
                    {
                        return ctx.getSource().respond("Don't know how to handle NumberSetting type %s", type);
                    }
                    return ctx.getSource().respond("Set %s.%s to \"%s\"", name, setting.getName(), setting.getValue());
                }));
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T>> void registerEnumSetting(final LiteralArgumentBuilder<CommandSource> literal, final EnumSetting<T> setting)
    {
        final Enum<T>[] constants = setting.getType().getEnumConstants();
        for (final Enum<?> constant : constants)
        {
            final String formatted = EnumSettingComponent.formatEnum(constant);
            literal.then(literal(formatted.replace(" ", ""))
                    .executes((ctx) ->
                    {
                        setting.setValue((T) constant);
                        return ctx.getSource().respond("Set %s.%s to \"%s\"", name, setting.getName(), formatted);
                    }));
        }
        literal.executes((ctx) ->
        {
            final StringJoiner joiner = new StringJoiner(", ");
            for (final Enum<T> constant : constants)
            {
                joiner.add(EnumSettingComponent.formatEnum(constant).replace(" ", ""));
            }
            return ctx.getSource().respond("%s.%s has %s enums: %s",
                    name, setting.getName(), constants.length, joiner.toString());
        });
    }

    private void registerStringSetting(final LiteralArgumentBuilder<CommandSource> literal, final Setting<String> setting)
    {
        literal.then(argument("value", StringArgumentType.greedyString())
                .executes((ctx) ->
                {
                    setting.setValue(StringArgumentType.getString(ctx, "value"));
                    return ctx.getSource().respond("Set %s.%s to \"%s\"", name, setting.getName(), setting.getValue());
                }));
    }

    private void registerColorSetting(final LiteralArgumentBuilder<CommandSource> literal, final ColorSetting setting)
    {
        if (setting.isAllowTransparency())
        {
            literal.then(argument("a", IntegerArgumentType.integer(0, 255))
                    .executes((ctx) ->
                    {
                        final int a = IntegerArgumentType.getInteger(ctx, "a");
                        final Color value = setting.getValue();
                        final Color color = new Color(value.getRed(), value.getGreen(), value.getBlue(), a);
                        setting.setValue(color);
                        return ctx.getSource().respond("Set %s.%s alpha to %s", name, setting.getName(), a);
                    }));
        }

        if (!setting.isExemptClientSync())
        {
            literal.then(literal("clientSync")
                    .then(argument("value", BoolArgumentType.bool())
                            .executes((ctx) ->
                            {
                                setting.setClientSync(BoolArgumentType.getBool(ctx, "value"));
                                return ctx.getSource().respond("Set %s.%s.ClientSync to %s",
                                        name, setting.getName(), setting.isClientSync());
                            }))
                    .executes((ctx) ->
                            ctx.getSource().respond("%s.%s.ClientSync = %s",
                                    name, setting.getName(), setting.isClientSync())));
        }

        literal.then(argument("r", IntegerArgumentType.integer(0, 255))
                        .then(argument("g", IntegerArgumentType.integer(0, 255))
                                .then(argument("b", IntegerArgumentType.integer(0, 255))
                                        .executes((ctx) ->
                                        {
                                            final int r = IntegerArgumentType.getInteger(ctx, "r");
                                            final int g = IntegerArgumentType.getInteger(ctx, "g");
                                            final int b = IntegerArgumentType.getInteger(ctx, "b");
                                            final Color color = new Color(r, g, b, setting.getValue().getAlpha());
                                            setting.setValue(color);
                                            return ctx.getSource().respond("Set %s.%s to RGB(%s, %s, %s)",
                                                    name, setting.getName(), r, g, b);
                                        }))))
                .executes((ctx) ->
                {
                    final Color value = setting.getValue();
                    return ctx.getSource().respond("%s.%s = RGBA(%s, %s, %s, %s)",
                            name, setting.getName(), value.getRed(), value.getGreen(), value.getBlue(), value.getAlpha());
                });
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { name.toLowerCase() };
    }

    @Override
    public String getDescription()
    {
        return "Configures " + name + " through a command";
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }
}
