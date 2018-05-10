package com.airbnb.lottie.utils;

import android.animation.ValueAnimator;
import android.support.annotation.FloatRange;

import com.airbnb.lottie.LottieComposition;

/**
 * @author houmingzhi(houmingzhi@didichuxing.com)
 * @since 2018/5/10.
 */

public abstract class AbsLottieAnimator extends ValueAnimator {

  public abstract void setComposition(LottieComposition composition);

  public abstract void clearComposition();

  @FloatRange(from = 0f, to = 1f) public abstract float getAnimatedValueAbsolute();

  public abstract void playAnimation();

  public abstract void resumeAnimation();

  public abstract void pauseAnimation();

  public abstract void endAnimation();

  public abstract void reverseAnimationSpeed();

  public abstract void setSpeed(float speed);

  public abstract float getSpeed();

  public abstract void setFrame(int frame);

  public abstract float getFrame();

  public abstract void setMinFrame(int minFrame);

  public abstract float getMinFrame();

  public abstract void setMaxFrame(int maxFrame);

  public abstract float getMaxFrame();

  public abstract void setMinAndMaxFrames(int minFrame, int maxFrame);
}
