package com.typewritermc.advancedblocks.entries.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.extension.annotations.*
import com.typewritermc.core.utils.point.Position
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.utils.toBukkitLocation
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.Directional
import org.bukkit.block.sign.Side
import org.bukkit.Bukkit
import java.util.Optional

val SIGN_MATERIALS = setOf(
    Material.OAK_WALL_SIGN, Material.SPRUCE_WALL_SIGN, Material.BIRCH_WALL_SIGN,
    Material.JUNGLE_WALL_SIGN, Material.ACACIA_WALL_SIGN, Material.DARK_OAK_WALL_SIGN,
    Material.MANGROVE_WALL_SIGN, Material.CHERRY_WALL_SIGN, Material.BAMBOO_WALL_SIGN,
    Material.CRIMSON_WALL_SIGN, Material.WARPED_WALL_SIGN,
    Material.OAK_SIGN, Material.SPRUCE_SIGN, Material.BIRCH_SIGN,
    Material.JUNGLE_SIGN, Material.ACACIA_SIGN, Material.DARK_OAK_SIGN,
    Material.MANGROVE_SIGN, Material.CHERRY_SIGN, Material.BAMBOO_SIGN,
    Material.CRIMSON_SIGN, Material.WARPED_SIGN,
)

data class SignNbtData(
    @Help("Строка 1 лицевой стороны")
    val frontLine1: String = "",
    @Help("Строка 2 лицевой стороны")
    val frontLine2: String = "",
    @Help("Строка 3 лицевой стороны")
    val frontLine3: String = "",
    @Help("Строка 4 лицевой стороны")
    val frontLine4: String = "",
    @Help("Цвет текста лицевой стороны (BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN, SILVER, GRAY, PINK, LIME, YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE)")
    val frontColor: String = "BLACK",
    @Help("Светящийся текст на лицевой стороне?")
    val frontGlowing: Boolean = false,
    @Help("Строка 1 обратной стороны")
    val backLine1: String = "",
    @Help("Строка 2 обратной стороны")
    val backLine2: String = "",
    @Help("Строка 3 обратной стороны")
    val backLine3: String = "",
    @Help("Строка 4 обратной стороны")
    val backLine4: String = "",
    @Help("Цвет текста обратной стороны")
    val backColor: String = "BLACK",
    @Help("Светящийся текст на обратной стороне?")
    val backGlowing: Boolean = false,
    @Help("Покрыта ли табличка воском?")
    val isWaxed: Boolean = false,
)

@Entry(
    "advanced_set_block",
    "Set a sign block with facing and optional NBT data",
    Colors.RED,
    "fluent:cube-add-20-filled"
)
class AdvancedSetBlockActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @MaterialProperties(MaterialProperty.BLOCK)
    @Help("Материал блока")
    val material: Var<Material> = ConstVar(Material.OAK_WALL_SIGN),
    @Help("Позиция, где будет установлен блок")
    val location: Var<Position> = ConstVar(Position.ORIGIN),
    @Help("Направление блока (NORTH, SOUTH, EAST, WEST, UP, DOWN)")
    val facing: BlockFace = BlockFace.NORTH,
    @Help("Применить NBT данные таблички?")
    val useNbt: Boolean = false,
    @Help("NBT данные таблички: строки, цвет, glowing, waxed.")
    val signNbt: Optional<SignNbtData> = Optional.empty(),
) : ActionEntry {

    override fun ActionTrigger.execute() {
        val mat = material.get(player, context)
        val bukkitLocation = location.get(player, context).toBukkitLocation()
        val plugin = Bukkit.getPluginManager().getPlugin("Typewriter")
            ?: return

        Bukkit.getScheduler().runTask(plugin, Runnable {
            val block = bukkitLocation.block
            block.type = mat

            val blockData = block.blockData
            if (blockData is Directional) {
                blockData.facing = facing
                block.blockData = blockData
            }

            if (useNbt && signNbt.isPresent && mat in SIGN_MATERIALS) {
                val blockState = block.state
                if (blockState is Sign) {
                    applySignNbt(blockState, signNbt.get())
                    blockState.update(true, false)
                }
            }
        })
    }
}

private fun applySignNbt(sign: Sign, nbt: SignNbtData) {
    val front = sign.getSide(Side.FRONT)
    val back = sign.getSide(Side.BACK)

    front.setLine(0, nbt.frontLine1)
    front.setLine(1, nbt.frontLine2)
    front.setLine(2, nbt.frontLine3)
    front.setLine(3, nbt.frontLine4)
    front.isGlowingText = nbt.frontGlowing
    runCatching { front.color = DyeColor.valueOf(nbt.frontColor.uppercase()) }

    back.setLine(0, nbt.backLine1)
    back.setLine(1, nbt.backLine2)
    back.setLine(2, nbt.backLine3)
    back.setLine(3, nbt.backLine4)
    back.isGlowingText = nbt.backGlowing
    runCatching { back.color = DyeColor.valueOf(nbt.backColor.uppercase()) }

    sign.isWaxed = nbt.isWaxed
}