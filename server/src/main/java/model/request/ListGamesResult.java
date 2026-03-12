package model.request;

import java.util.List;

public record ListGamesResult(List<GameListItem> games) {
}
