package com.kopirealm.peasyrecyclerview;

/**
 * Extended Blueprint of @{@link PeasyCoordinatorContent}
 * Coordination : Header section of list
 *
 * @param <D>
 */
public abstract class PeasyHeaderContent<D> extends PeasyCoordinatorContent<PeasyHeaderViewHolder, D> {
    public PeasyHeaderContent(int viewtypeId, D data) {
        super(viewtypeId, data);
    }
}
