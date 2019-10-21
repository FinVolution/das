package com.ppdai.das.core;

import com.ppdai.das.core.exceptions.DalException;

/**
 * Setup at global level to simplify 
 * 
 * @author jhhe
 *
 */
public interface DatabaseSelector extends DasComponent {
    String select(SelectionContext context) throws DalException;
}
