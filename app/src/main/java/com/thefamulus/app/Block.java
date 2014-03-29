package com.thefamulus.app;

import android.view.View;

/**
 * Created by martin on 26/03/14.
 */
public class Block {

    // Must have a view
    View blockView;
    int blockPriority = 0;
    int blockRefresh = 0;
    // TODO onClick method



    // Auto refresh interval
    int autoRefreshInterval = 0;

    public Block(View mustHaveView) {
        // Create
        blockView = mustHaveView;
    }

}
