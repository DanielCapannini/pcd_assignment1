package pcd.base;


import pcd.engine.Action;

/**
 * Car agent move forward action
 */
public record MoveForward(double distance) implements Action {}
