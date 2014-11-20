package app.se329.project2.tools;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public abstract class AnimationEndListener implements AnimationListener{

	@Override
	public void onAnimationStart(Animation animation) {
		
	}

	@Override
	public abstract void onAnimationEnd(Animation animation);

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

}
