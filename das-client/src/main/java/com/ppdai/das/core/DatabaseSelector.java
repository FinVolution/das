package com.ppdai.das.core;

/**
 * Setup at global level to simplify 
 * 
 * @author jhhe
 *
 */
public interface DatabaseSelector extends DasComponent {
    String select(SelectionContext context) throws DasException;
}
