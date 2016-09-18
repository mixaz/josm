// License: GPL. For details, see Readme.txt file.
package org.openstreetmap.gui.jmapviewer.tilesources;

import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.TileXY;
import org.openstreetmap.gui.jmapviewer.YandexUtils;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

/**
 * TMS tile source.
 */
public class TMSTileSource extends AbstractTMSTileSource {

    protected int maxZoom;
    protected int minZoom;
    protected OsmMercator osmMercator;

    private boolean useYandexMercator = false;

    /**
     * Constructs a new {@code TMSTileSource}.
     * @param info tile source information
     */
    public TMSTileSource(TileSourceInfo info) {
        super(info);
        minZoom = info.getMinZoom();
        maxZoom = info.getMaxZoom();
        this.osmMercator = new OsmMercator(this.getTileSize());
    }

    public void enableYandexMercator(boolean enable) {
        useYandexMercator = enable;
    }

    @Override
    public int getMinZoom() {
        return (minZoom == 0) ? super.getMinZoom() : minZoom;
    }

    @Override
    public int getMaxZoom() {
        return (maxZoom == 0) ? super.getMaxZoom() : maxZoom;
    }

    @Override
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        return osmMercator.getDistance(lat1, lon1, lat2, lon2);
    }

    @Override
    public Point latLonToXY(double lat, double lon, int zoom) {
        Point pp;
        if(useYandexMercator) {
            double[] mercator = YandexUtils.geoToMercator(new double[] {lon, lat });
            double[] tiles = YandexUtils.mercatorToTiles(mercator);
//        long xy[] = YandexUtils.getTileFromGeo(lat,lon,zoom);
            pp = new Point(
                    (int) tiles[0],
                    (int) tiles[1]
            );
        }
        else {
            pp = new Point(
                    (int) osmMercator.lonToX(lon, zoom),
                    (int) osmMercator.latToY(lat, zoom)
            );
        }
//        System.out.println("latLonToXY lat=" +lat+", lon="+lon+", zoom="+zoom+ ": p1="+ p1+" p2="+p2);
        return pp;
    }

    @Override
    public ICoordinate xyToLatLon(int x, int y, int zoom) {
        Coordinate cc;
        if(useYandexMercator) {
            cc = new Coordinate(
                    YandexUtils.pixels2lat(y,zoom),
                    YandexUtils.pixels2lon(x,zoom)
            );
        }
        else {
            cc = new Coordinate(
                    osmMercator.yToLat(y, zoom),
                    osmMercator.xToLon(x, zoom)
            );
        }
//        System.out.println("xyToLatLon x=" +x+", y="+y+", zoom="+zoom+ ": cc1="+ cc1+" cc2="+cc2);
        return cc;
    }

    @Override
    public TileXY latLonToTileXY(double lat, double lon, int zoom) {
        TileXY tileXY;
        if(useYandexMercator) {
            double xy[] = YandexUtils.getMapTileFromCoordinates(lat,lon,zoom);
            tileXY = new TileXY(
                    xy[1],
                    xy[0]
            );
        }
        else {
            tileXY = new TileXY(
                    osmMercator.lonToX(lon, zoom) / getTileSize(),
                    osmMercator.latToY(lat, zoom) / getTileSize()
            );
        }
//        System.out.println("latLonToTileXY lat=" +lat+", lon="+lon+", zoom="+zoom+ ": p1="+ tileXY1+" p2="+tileXY2);
        return tileXY;
    }

    @Override
    public ICoordinate tileXYToLatLon(int x, int y, int zoom) {
        Coordinate cc;
        if(useYandexMercator) {
            cc = new Coordinate(
                    YandexUtils.tile2lat(y,zoom),
                    YandexUtils.tile2lon(x,zoom)
            );
        }
        else {
            cc = new Coordinate(
                    osmMercator.yToLat(y * getTileSize(), zoom),
                    osmMercator.xToLon(x * getTileSize(), zoom)
            );
        }
//        System.out.println("tileXYToLatLon x=" +x+", y="+y+", zoom="+zoom+ ": cc1="+ cc1+" cc2="+cc2);
        return cc;
    }
}
