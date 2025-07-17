package com.badlogic.pruebas.ayudas.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

public class LoadTileMap implements Disposable {

    private TiledMap tiledMap;
    private float renderScale;

    private float mapWidth;
    private float mapHeight;
    private float tileOriginalWidth;
    private float tileOriginalHeight;

    private int mapTilesWide;
    private int mapTilesHigh;

//    private float renderScale; // La escala que se usa en OrthogonalTiledMapRenderer


    public LoadTileMap(String pathTilemap, float renderScale) {
        tiledMap = new TmxMapLoader().load(pathTilemap);
        this.renderScale = renderScale;


        // --- Obtener las dimensiones del mapa y tiles originales ---
        tileOriginalWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        tileOriginalHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        mapTilesWide = tiledMap.getProperties().get("width", Integer.class);
        mapTilesHigh = tiledMap.getProperties().get("height", Integer.class);

        // Calcular el tamaño total del mapa en unidades de mundo, teniendo en cuenta la escala del renderizador
        mapWidth = mapTilesWide * tileOriginalWidth * renderScale;
        mapHeight = mapTilesHigh * tileOriginalHeight * renderScale;
    }


    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public float getRenderScale() {
        return renderScale;
    }

    public float getMapWidth() {
        return mapWidth;
    }

    public float getMapHeight() {
        return mapHeight;
    }


    @Override
    public void dispose() {
        tiledMap.dispose();
    }
}
