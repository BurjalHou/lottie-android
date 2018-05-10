package com.airbnb.lottie.utils;

import android.animation.ValueAnimator;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieComposition;

/**
 * This is a slightly modified {@link ValueAnimator} that allows us to update start and end values
 * easily optimizing for the fact that we know that it's a value animator with 2 floats.
 */

public class LottieSanMR1ValueAnimator extends AbsLottieAnimator {
  private boolean systemAnimationsAreDisabled = false;
  private float compositionDuration;
  private float speed = 1f;
  @FloatRange(from = 0f, to = 1f) private float value = 0f;
  @FloatRange(from = 0f, to = 1f) private float minValue = 0f;
  @FloatRange(from = 0f, to = 1f) private float maxValue = 1f;
  @Nullable private LottieComposition composition;

  public LottieSanMR1ValueAnimator() {
    setInterpolator(null);
    addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(ValueAnimator animation) {
        // On older devices, getAnimatedValue and getAnimatedFraction
        // will always return 0 if animations are disabled.
        if (!systemAnimationsAreDisabled) {
          value = (float) animation.getAnimatedValue();
        }
      }
    });
    updateValues();
  }

  public void systemAnimationsAreDisabled() {
    this.systemAnimationsAreDisabled = true;
  }

  public void setCompositionDuration(float compositionDuration) {
    this.compositionDuration = compositionDuration;
    updateValues();
  }

  /**
   * Sets the current animator value. This will update the play time as well.
   * It will also be clamped to the values set with {@link #setMinValue(float)} and
   * {@link #setMaxValue(float)}.
   */
  public void setValue(@FloatRange(from = 0f, to = 1f) float value) {
    value = MiscUtils.clamp(value, minValue, maxValue);

    this.value = value;
    float distFromStart = isReversed() ? (maxValue - value) : (value - minValue);
    float range = Math.abs(maxValue - minValue);
    float animatedPercentage = distFromStart / range;
    if (getDuration() > 0) {
      setCurrentPlayTime(Math.round(getDuration() * animatedPercentage));
    }
  }

  public float getValue() {
    return value;
  }

  public void setMinAndMaxValues(
      @FloatRange(from = 0f, to = 1f) float minValue,
      @FloatRange(from = 0f, to = 1f) float maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
    updateValues();
  }

  public void setMinValue(@FloatRange(from = 0f, to = 1f) float minValue) {
    if (minValue >= maxValue) {
      throw new IllegalArgumentException("Min value must be smaller then max value.");
    }
    this.minValue = minValue;
    updateValues();
  }

  public float getMinValue() {
    return minValue;
  }

  public void setMaxValue(@FloatRange(from = 0f, to = 1f) float maxValue) {
    if (maxValue <= minValue) {
      throw new IllegalArgumentException("Max value must be greater than min value.");
    }
    this.maxValue = maxValue;
    updateValues();
  }

  @Override public void reverseAnimationSpeed() {
    setSpeed(-getSpeed());
  }

  @Override public void setSpeed(float speed) {
    this.speed = speed;
    updateValues();
  }

  @Override public float getSpeed() {
    return speed;
  }

  @Override public void playAnimation() {
    start();
    setValue(isReversed() ? maxValue : minValue);
  }

  @Override public void endAnimation() {
    end();
  }

  @Override public void setFrame(int frame) {
    if (null == composition) {
      return;
    }
    setValue(frame / composition.getDurationFrames());
  }

  @Override public float getFrame() {
    if (null == composition) {
      return 0;
    }
    return Math.round(getValue() * composition.getDurationFrames());
  }

  @Override public void setMinFrame(int minFrame) {
    if (null == composition) {
      return;
    }
    setMinValue(minFrame / composition.getDurationFrames());
  }

  @Override public float getMinFrame() {
    if (null == composition) {
      return 0;
    }
    return minValue * composition.getDurationFrames();
  }

  @Override public void setMaxFrame(int maxFrame) {
    if (null == composition) {
      return;
    }
    setMaxValue(maxFrame / composition.getDurationFrames());
  }

  @Override public float getMaxFrame() {
    if (null == composition) {
      return 0;
    }
    return maxValue * composition.getDurationFrames();
  }

  @Override public void setMinAndMaxFrames(int minFrame, int maxFrame) {
    if (null == composition) {
      return;
    }
    setMinAndMaxValues(minFrame / composition.getDurationFrames(), maxFrame / composition.getDurationFrames());
  }

  @Override public void pauseAnimation() {
    float value = this.value;
    cancel();
    setValue(value);
  }

  @Override public void resumeAnimation() {
    float value = this.value;
    if (isReversed() && this.value == minValue) {
      value = maxValue;
    } else if (!isReversed() && this.value == maxValue) {
      value = minValue;
    }
    start();
    setValue(value);
  }

  private boolean isReversed() {
    return speed < 0;
  }

  /**
   * Update the float values of the animator, scales the duration for the current min/max range
   * and updates the play time so that it matches the new min/max range.
   */
  private void updateValues() {
    setDuration((long) (compositionDuration * (maxValue - minValue) / Math.abs(speed)));
    setFloatValues(
        speed < 0 ? maxValue : minValue,
        speed < 0 ? minValue : maxValue
    );
    // This will force the play time to be correct for the current value.
    setValue(value);
  }

  @Override public void setComposition(LottieComposition composition) {
    this.composition = composition;
    setCompositionDuration(composition.getDuration());
  }

  @Override public void clearComposition() {
    composition = null;
  }

  @Override @FloatRange(from = 0f, to = 1f) public float getAnimatedValueAbsolute() {
    return getValue();
  }

  @Override @FloatRange(from = 0f, to = 1f) public float getAnimatedFraction() {
    return getValue();
  }
}

