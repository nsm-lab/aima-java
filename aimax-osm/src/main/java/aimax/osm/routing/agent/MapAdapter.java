package aimax.osm.routing.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import aima.core.environment.map.Map;
import aima.core.util.Util;
import aima.core.util.datastructure.Point2D;
import aimax.osm.data.BoundingBox;
import aimax.osm.data.MapWayFilter;
import aimax.osm.data.OsmMap;
import aimax.osm.data.Position;
import aimax.osm.data.entities.MapNode;
import aimax.osm.data.entities.MapWay;
import aimax.osm.data.entities.WayRef;

/**
 * Adapter class which provides an aima-core <code>Map</code> interface for
 * instances of type <code>OsmMap</code>. This enables to create map
 * environments based on real OSM data. Note that location strings are
 * dynamically generated from map node ids (long values), so always use the
 * equal method for comparison.
 * 
 * @author Ruediger Lunde
 */
public class MapAdapter implements Map {

	/** A map which is generated from OSM data. */
	OsmMap osmMap;
	/**
	 * A filter, which hides some of the ways (e.g. foot ways are irrelevant
	 * when traveling by car.).
	 */
	MapWayFilter filter;
	/**
	 * Controls whether a way which is marked as one-way can be traveled in both
	 * directions.
	 */
	boolean ignoreOneways;

	public MapAdapter(OsmMap map) {
		this.osmMap = map;
	}

	public void ignoreOneways(boolean state) {
		ignoreOneways = state;
	}

	public void setMapWayFilter(MapWayFilter filter) {
		this.filter = filter;
	}

	public OsmMap getOsmMap() {
		return osmMap;
	}

	@Override
	public Double getDistance(String fromLocation, String toLocation) {
		MapNode node1 = getWayNode(fromLocation);
		MapNode node2 = getWayNode(toLocation);
		if (node1 != null && node2 != null
				&& getLocationsLinkedTo(fromLocation).contains(toLocation))
			return new Position(node1).getDistKM(node2);
		else
			return null;
	}

	/** {@inheritDoc} Very expensive for large maps! */
	@Override
	public List<String> getLocations() {
		List<String> result = new ArrayList<String>();
		HashSet<MapNode> nodeHash = new HashSet<MapNode>();
		for (MapWay way : osmMap.getWays(new BoundingBox(-90, -180, 90, 180))) {
			if (filter == null || filter.isAccepted(way)) {
				for (MapNode node : way.getNodes())
					if (!nodeHash.contains(node)) {
						result.add(Long.toString(node.getId()));
						nodeHash.add(node);
					}
			}
		}
		return result;
	}

	@Override
	public List<String> getLocationsLinkedTo(String fromLocation) {
		List<String> result = new ArrayList<String>();
		MapNode node = getWayNode(fromLocation);
		if (node != null) {
			for (WayRef wref : node.getWayRefs()) {
				if (filter == null || filter.isAccepted(wref.getWay())) {
					MapWay way = wref.getWay();
					int nodeIdx = wref.getNodeIdx();
					List<MapNode> wayNodes = way.getNodes();
					MapNode next;
					if (wayNodes.size() > nodeIdx + 1) {
						next = wayNodes.get(nodeIdx + 1);
						result.add(Long.toString(next.getId()));
					}
					if (nodeIdx > 0 && (!way.isOneway() || ignoreOneways)) {
						next = wayNodes.get(nodeIdx - 1);
						result.add(Long.toString(next.getId()));
					}
				}
			}
		}
		return result;
	}

	/** Returns a <code>PointLatLon</code> instance. */
	@Override
	public Point2D getPosition(String loc) {
		MapNode node = getWayNode(loc);
		if (node != null)
			return new PointLatLon(node.getLat(), node.getLon());
		else
			return null;
	}

	@Override
	public String randomlyGenerateDestination() {
		return Util.selectRandomlyFromList(getLocations());
	}

	/**
	 * Returns the ID of the way node in the underlying OSM map which is nearest
	 * with respect to the specified coordinates and additionally passes the
	 * filter.
	 */
	public String getNearestLocation(Point2D pt) {
		Position pos = new Position((float) pt.getY(), (float) pt.getX());
		MapNode node = osmMap.getNearestWayNode(pos, filter);
		return (node != null) ? Long.toString(node.getId()) : null;
	}

	/** Returns the OSM way node corresponding to the given location string. */
	public MapNode getWayNode(String id) {
		MapNode result = null;
		try {
			result = osmMap.getNode(Long.parseLong(id));
		} catch (NumberFormatException e) {
			// node not found, indicated by return value null.
		}
		return result;
	}
}
