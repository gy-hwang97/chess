package server;

public record GameRequestBody(String gameName, String playerColor, Integer gameID) {
}