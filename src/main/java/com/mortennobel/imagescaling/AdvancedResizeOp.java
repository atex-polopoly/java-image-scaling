/*
 * Copyright 2013, Morten Nobel-Joergensen
 *
 * License: The BSD 3-Clause License
 * http://opensource.org/licenses/BSD-3-Clause
 */
package com.mortennobel.imagescaling;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jhlabs.image.UnsharpFilter;
import com.mortennobel.imagescaling.threads.ResampleThreadFactory;

/**
 * @author Morten Nobel-Joergensen
 */
public abstract class AdvancedResizeOp implements BufferedImageOp {
	public static enum UnsharpenMask{
		None(0),
		Soft(0.15f),
		Normal(0.3f),
		VerySharp(0.45f),
		Oversharpened(0.60f);
		private final float factor;

		UnsharpenMask(float factor) {
			this.factor = factor;
		}
	}
	private List<ProgressListener> listeners = new ArrayList<ProgressListener>();

    private final DimensionConstrain dimensionConstrain;
    private final ExecutorService executorService;
	private UnsharpenMask unsharpenMask = UnsharpenMask.None;

	public AdvancedResizeOp(DimensionConstrain dimensionConstrain) {
		this(dimensionConstrain, Executors.newCachedThreadPool(new ResampleThreadFactory()));
	}

	public AdvancedResizeOp(final DimensionConstrain dimensionConstrain,
							final ExecutorService executorService) {
		this.dimensionConstrain = dimensionConstrain;
		this.executorService = executorService;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public UnsharpenMask getUnsharpenMask() {
		return unsharpenMask;
	}

	public void setUnsharpenMask(UnsharpenMask unsharpenMask) {
		this.unsharpenMask = unsharpenMask;
	}

	protected void fireProgressChanged(float fraction){
        for (ProgressListener progressListener:listeners){
            progressListener.notifyProgress(fraction);
        }
    }

    public final void addProgressListener(ProgressListener progressListener) {
        listeners.add(progressListener);
    }

    public final boolean removeProgressListener(ProgressListener progressListener) {
        return listeners.remove(progressListener);
    }

    public final BufferedImage filter(BufferedImage src, BufferedImage dest){
		Dimension dstDimension = dimensionConstrain.getDimension(new  Dimension(src.getWidth(),src.getHeight()));
		int dstWidth = dstDimension.width;
		int dstHeight = dstDimension.height;
		BufferedImage bufferedImage = doFilter(src, dest, dstWidth, dstHeight);

		if (unsharpenMask!= UnsharpenMask.None){
			UnsharpFilter unsharpFilter= new UnsharpFilter();
			unsharpFilter.setRadius(2f);
			unsharpFilter.setAmount(unsharpenMask.factor);
			unsharpFilter.setThreshold(10);
			return  unsharpFilter.filter(bufferedImage, null);
		}

		return bufferedImage;
	}

	protected abstract BufferedImage doFilter(BufferedImage src, BufferedImage dest, int dstWidth, int dstHeight);

	/**
     * {@inheritDoc}
     */
    public final Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public final BufferedImage createCompatibleDestImage(BufferedImage src,
                                                   ColorModel destCM) {
        if (destCM == null) {
            destCM = src.getColorModel();
        }
        return new BufferedImage(destCM,
                                 destCM.createCompatibleWritableRaster(
                                         src.getWidth(), src.getHeight()),
                                 destCM.isAlphaPremultiplied(), null);
    }

    /**
     * {@inheritDoc}
     */
    public final Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return (Point2D) srcPt.clone();
    }

    /**
     * {@inheritDoc}
     */
    public final RenderingHints getRenderingHints() {
        return null;
    }
}
