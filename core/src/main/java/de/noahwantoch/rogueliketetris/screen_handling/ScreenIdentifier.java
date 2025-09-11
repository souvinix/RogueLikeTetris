package de.noahwantoch.rogueliketetris.screen_handling;

import java.util.Objects;

import de.noahwantoch.rogueliketetris.main.GameHandler;
import de.noahwantoch.rogueliketetris.screens.BaseScreen;
import de.noahwantoch.rogueliketetris.screens.GameNormalScreen;
import de.noahwantoch.rogueliketetris.screens.GameRogueLikeScreen;
import de.noahwantoch.rogueliketetris.screens.LeaderboardScreen;
import de.noahwantoch.rogueliketetris.screens.MenuScreen;
import de.noahwantoch.rogueliketetris.screens.ModesScreen;
import de.noahwantoch.rogueliketetris.screens.OptionsScreen;
import de.noahwantoch.rogueliketetris.screens.ShopScreen;

public interface ScreenIdentifier {
    enum ScreenType{
        MENU,
        MODES,
        GAME_NORMAL,
        GAME_ROGUELIKE,
        OPTIONS,
        LEADERBOARDS,
        SHOP
    }
    default ScreenType getType(BaseScreen screen){
        if (screen instanceof MenuScreen) {
            return ScreenType.MENU;
        } else if (screen instanceof ModesScreen) {
            return ScreenType.MODES;
        } else if (screen instanceof OptionsScreen) {
            return ScreenType.OPTIONS;
        } else if (screen instanceof GameNormalScreen) {
            return ScreenType.GAME_NORMAL;
        } else if (screen instanceof GameRogueLikeScreen) {
            return ScreenType.GAME_ROGUELIKE;
        } else if (screen instanceof LeaderboardScreen) {
            return ScreenType.LEADERBOARDS;
        } else if (screen instanceof ShopScreen) {
            return ScreenType.SHOP;
        }
        return null;
    }

    static BaseScreen getScreen(ScreenType type, GameHandler game){
        if(type == ScreenType.MENU){
            return new MenuScreen(game);
        }else if(type == ScreenType.MODES){
            return new ModesScreen(game);
        }else if(type == ScreenType.GAME_NORMAL){
            return new GameNormalScreen(game);
        }else if(type == ScreenType.GAME_ROGUELIKE){
            return new GameRogueLikeScreen(game);
        }else if(type == ScreenType.OPTIONS){
            return new OptionsScreen(game);
        }else if(type == ScreenType.LEADERBOARDS){
            return new LeaderboardScreen(game);
        }else if(type == ScreenType.SHOP){
            return new ShopScreen(game);
        }else return null;
    }
}
