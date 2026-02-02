package dev.chasem.cobblemonextras.events

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.events.pokemon.PokemonCapturedEvent
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.util.math.geometry.toRadians
import dev.chasem.cobblemonextras.CobblemonExtras
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.math.cos

class CobblemonExtrasEventHandler {

    fun onPokemonCapture(event: PokemonCapturedEvent) {
        val pokemon = event.pokemon
        val pokeBallEntity = event.pokeBallEntity
        if (pokeBallEntity.tags.contains("shinyBall")) {
            pokemon.shiny = true
        }
    }

    fun onUseItem(player: ServerPlayer, world: Level, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(hand)
        if (itemStack.item == CobblemonItems.POKE_BALL || itemStack.item == CobblemonItems.GREAT_BALL || itemStack.item == CobblemonItems.ULTRA_BALL || itemStack.item == CobblemonItems.MASTER_BALL) {
            if (itemStack.has(DataComponents.CUSTOM_DATA)) {
                val customData = itemStack.get(DataComponents.CUSTOM_DATA)
                val tag = customData?.copyTag() ?: CompoundTag()
                if (tag.contains("CobblemonExtrasBallType")) {
                    val ballType = tag.getString("CobblemonExtrasBallType")
                    if (ballType == "shiny") {

                        val itemBallType = tag.getString("ShinyBallBallType")
                        val pokeBall = when(itemBallType) {
                            "poke" -> CobblemonItems.POKE_BALL.pokeBall
                            "great" -> CobblemonItems.GREAT_BALL.pokeBall
                            "ultra" -> CobblemonItems.ULTRA_BALL.pokeBall
                            "master" -> CobblemonItems.MASTER_BALL.pokeBall
                            else -> CobblemonItems.POKE_BALL.pokeBall
                        }
                        val pokeBallEntity = EmptyPokeBallEntity(pokeBall, player.level(), player)


                        pokeBallEntity.apply {
                            val overhandFactor: Float = if (player.xRot < 0) {
                                5f * cos(player.xRot.toRadians())
                            } else {
                                5f
                            }

                            shootFromRotation(player, player.xRot - overhandFactor, player.yRot, 0.0f, pokeBall.throwPower, 1.0f)
                            setPos(position().add(deltaMovement.normalize().scale(1.0)))
                            owner = player

                        }
                        pokeBallEntity.setGlowingTag(true)
                        pokeBallEntity.addTag("shinyBall")
                        pokeBallEntity.aspects += "shinyBall"
                        world.addFreshEntity(pokeBallEntity)
                        itemStack.shrink(1)
                    }
                    return InteractionResultHolder.fail(itemStack)
                }
            }
        }

        return InteractionResultHolder.pass(itemStack)
    }
}
