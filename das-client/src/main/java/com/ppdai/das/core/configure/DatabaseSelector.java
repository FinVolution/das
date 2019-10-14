package com.ppdai.das.core.configure;

import com.ppdai.das.core.exceptions.DalException;

/**
 * Setup at global level to simplify 
 * 
 * @author jhhe
 *
 */
public interface DatabaseSelector extends DalComponent {
    String select(SelectionContext context) throws DalException;
}
