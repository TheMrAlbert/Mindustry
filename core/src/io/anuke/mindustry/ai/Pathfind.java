package io.anuke.mindustry.ai;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.anuke.mindustry.entities.enemies.Enemy;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.World;
import io.anuke.ucore.core.Timers;
public class Pathfind{
	static MHueristic heuristic = new MHueristic();
	static PassTileGraph graph = new PassTileGraph();
	static PathFinder<Tile> finder = new IndexedAStarPathFinder<Tile>(graph);
	static PathSmoother<Tile, Vector2> smoother = new PathSmoother<Tile, Vector2>(new Raycaster());
	static Vector2 vector = new Vector2();
	
	static public Vector2 find(Enemy enemy){
		findNode(enemy);
		
		if(enemy.node <= -1) return vector.set(enemy.x, enemy.y);
		
		//-1 is only possible here if both pathfindings failed, which should NOT happen
		//check graph code
		
		//Tile[] path = enemy.path;
		Array<Tile> path = enemy.gpath.nodes;

		Tile target = path.get(enemy.node);
			
		float dst = Vector2.dst(enemy.x, enemy.y, target.worldx(), target.worldy());
			
		if(dst < 2){
			if(enemy.node <= path.size-2)
				enemy.node ++;
				
			target = path.get(enemy.node);
		}
		
		//near the core, stop
		if(enemy.node == path.size - 1){
			vector.set(target.worldx(), target.worldy());
		}
			
		return vector.set(target.worldx(), target.worldy());
		
	}
	
	static public void updatePath(){
		
		/*
		if(paths.size == 0 || paths.size != World.spawnpoints.size){
			paths.clear();
			pathSequences = new Tile[World.spawnpoints.size][0];
			for(int i = 0; i < World.spawnpoints.size; i ++){
				SmoothGraphPath path = new SmoothGraphPath();
				paths.add(path);
			}
		}
		
		for(int i = 0; i < paths.size; i ++){
			SmoothGraphPath path = paths.get(i);
			
			path.clear();
			passpathfinder.searchNodePath(
					World.spawnpoints.get(i), 
					World.core, heuristic, path);
			
			smoother.smoothPath(path);
			
			pathSequences[i] = new Tile[path.getCount()];
			
			for(int node = 0; node < path.getCount(); node ++){
				Tile tile = path.get(node);
				
				pathSequences[i][node] = tile;
			}
			
			
			if(Vars.debug && Vars.showPaths)
			for(Tile tile : path){
				Effects.effect(Fx.ind, tile.worldx(), tile.worldy());
			}
			
		}*/
	}
	
	static void findNode(Enemy enemy){
		/*
		enemy.path = pathSequences[enemy.spawn];
		Tile[] path = enemy.path;
		*/
		
		
		
		if(enemy.node == -1 || (Timers.get(enemy, "pathfind", 120) && enemy.request.pathFound)){
			
			enemy.gpath = new SmoothGraphPath();
			enemy.finder = new IndexedAStarPathFinder<Tile>(graph);
			enemy.gpath.clear();
			
			enemy.request = new PathFinderRequest<Tile>(World.tileWorld(enemy.x, enemy.y), 
					World.core, 
					heuristic, enemy.gpath);
			enemy.request.statusChanged = true;
			
			enemy.node = -2;
		}
		
		if(enemy.gpath != null && !enemy.request.pathFound){
			enemy.request.executionFrames ++;
			if(enemy.finder.search(enemy.request, 1000000 / 5)){
				smoother.smoothPath(enemy.gpath);
				enemy.node = 1;
				//UCore.log("done in " + enemy.request.executionFrames + " frames with path of size " + enemy.gpath.getCount());
			}
		}
		
		/*
		Tile closest = null;
		float ldst = 0f;
		int cindex = -1;
		
		for(int i = 0; i < path.size; i ++){
			Tile tile = path.get(i);
			float dst = Vector2.dst(tile.worldx(), tile.worldy(), enemy.x, enemy.y);
			
			if(closest == null || dst < ldst){
				ldst = dst;
				closest = tile;
				cindex = i;
			}
		}
		enemy.node = cindex;*/
	}
}
