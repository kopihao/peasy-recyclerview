package com.kopirealm.peasyrecyclerview;


/**
 * Extended Blueprint of @{@link PeasyCoordinatorContent}
 * Coordination : Content section of list
 *
 * @param <D> Data Type
 */
public abstract class PeasyFooterContent<D> extends PeasyCoordinatorContent<PeasyFooterViewHolder, D> {
    public PeasyFooterContent(int viewtypeId, D data) {
        super(viewtypeId, data);
    }
}
