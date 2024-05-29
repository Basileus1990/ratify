package net.darktree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

// TODO: Add smooth scrolling
class SmoothMouseWheelListener implements MouseWheelListener {
    Timer timer;
    int targetPosition;
    int currentIncrement;
    private long _lastUpdate = 0;
    private long _lastScroll = 0;
    private double _velocity = 0.0;
    private double _force = 0.0;
    private static final double STEP = 0.0004;
    private static final double MAX_FORCE = 0.08;
    private static final double PSEUDO_FRICTION = 0.93;
    private static final double ACC_REDUCTION_FACTOR = 0.8;
    private static final double SPEED_THRESHOLD = 0.001;

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (_lastScroll == 0) {
            _lastScroll = System.nanoTime();
            return;
        }

        long currentTime = System.nanoTime();
        double elapsedMillis = (currentTime - _lastScroll) * 1.0e-6;
        _lastScroll = currentTime;

        if (elapsedMillis == 0) { return; }

        double wheelDelta = e.getPreciseWheelRotation();
        boolean sameDirection = _velocity * wheelDelta >= 0;

        if (sameDirection) {
            double currentStep = wheelDelta * STEP;
            _force += currentStep + currentStep / (elapsedMillis * 0.0007);

            // Limit the magnitude of the force to MAX_FORCE.
            double forceMagnitude = Math.abs(_force);
            if (forceMagnitude > MAX_FORCE) { _force *= MAX_FORCE / forceMagnitude; }
        } else {
            _force = 0;
            _velocity = 0;
        }
    }
}
