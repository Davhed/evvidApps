/*
 * 
 * 		Shamrock Lane relies heavily on the Rajawali framework which can be found here:
 * 
 * 		https://github.com/MasDennis/Rajawali
 * 		
 * 		Rajawali --
 * 		Copyright 2011 Dennis Ippel
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *     
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.evvid.wallpapers.shamrocklane;

import rajawali.renderer.RajawaliRenderer;
import rajawali.util.MeshExporter;
import android.content.Context;

public class SceneExporter extends RajawaliRenderer{
	
	public SceneExporter(Context context) {
		super(context);
		setFrameRate(60);
    }
		
	public void initScene() {
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.arch, "arch.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.castle_towers, "castle_towers.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.castle, "castle.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.dirt, "dirt.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.door, "door.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.fgferns, "fgferns.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.fgtrees, "fgtrees.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers1, "flowers1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers2, "flowers2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers3, "flowers3.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers4, "flowers4.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers5, "flowers5.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers6, "flowers6.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.flowers7, "flowers7.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.gate, "gate.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.gold, "gold.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.grass1, "grass1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.grass2, "grass2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.grass3, "grass3.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.grass4, "grass4.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.grass5, "grass5.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.ground, "ground.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.largestump, "largestump.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.lilys, "lilys.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.path, "path.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.pondferns, "pondferns.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.pot, "pot.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.rbow1, "rbow1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.rbow2, "rbow2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.rocks, "rocks.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.shadows1, "shadows1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.shadows2, "shadows2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.shamrock, "shamrock.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.shamrocks, "shamrocks.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.shrooms, "shrooms.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.stump, "stump.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.stumpdecal, "stumpdecal.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.tree, "tree.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.treeferns, "treeferns.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.vines1, "vines1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.vines2, "vines2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.vines3, "vines3.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.walls, "walls.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.waterfall, "waterfall.ser");

//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_arches, "int_arches.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_books_rocker, "int_books_rocker.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_buckles, "int_buckles.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_candles, "int_candles.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_chair, "int_chair.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_chimneyring, "int_chimneyring.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clover_table, "int_clover_table.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers1, "int_clovers1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers2, "int_clovers2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers3, "int_clovers3.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers4, "int_clovers4.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers5, "int_clovers5.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers6, "int_clovers6.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_clovers7, "int_clovers7.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_coatrack, "int_coatrack.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_door_window, "int_door_window.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_door, "int_door.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_floor, "int_floor.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_gold, "int_gold.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_hat, "int_hat.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_lamp1, "int_lamp1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_lamp2, "int_lamp2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_large_table, "int_large_table.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_logs, "int_logs.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_mug, "int_mug.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_open_book, "int_open_book.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_pipe, "int_pipe.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_planter, "int_planter.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_rug, "int_rug.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_shelf, "int_shelf.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_shoes, "int_shoes.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_small_table, "int_small_table.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_stairs, "int_stairs.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_stone_pad, "int_stone_pad.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_stove, "int_stove.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_tablerunner, "int_tablerunner.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_tapestry, "int_tapestry.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_tapestry_rod, "int_tapestry_rod.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_tree, "int_tree.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_vase1, "int_vase1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_vase2, "int_vase2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_window1, "int_window1.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_window2, "int_window2.ser");
//		MeshExporter.serializeObj(mContext, mTextureManager, R.raw.int_window2_sill, "int_window2_sill.ser");

}
}