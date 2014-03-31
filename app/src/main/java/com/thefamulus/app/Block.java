package com.thefamulus.app;

import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by martin on 26/03/14.
 */
public class Block {

    // Must have a parent container
    // TODO parent LinearLayout
    LinearLayout blockParent;

    // Must have a view
    // TODO Assign view
    LinearLayout blockView;

    // Priority 0 lowest, 9 highest
    int blockPriority = 0;

    // Refresh period in seconds
    int blockRefresh = 0;

    // TODO onClick method

    // TODO onUpdate method

    public Block Block(LinearLayout parentView, LinearLayout childView) {
        // Create
        blockParent = parentView;
        blockView = childView;
        return this;
    }

    public void Activate() {
        // Activate the block
        blockParent.addView(blockView);
    }
}
