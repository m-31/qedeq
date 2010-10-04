package org.qedeq.kernel.bo.logic.model;

/**
 * A model for our mathematical world. It should have entities, functions and predicates.
 * There should also be predicate and function constants.
 *
 * @author  Michael Meyling
 */
public interface Model {

    /**
     * Get number of all entities in this model.
     *
     * @return  Number of entities.
     */
    public int getEntitiesSize();

    /**
     * Get entity <code>number</code>.
     *
     * @param   number  Get entity with this number.
     * @return  Entity.
     */
    public Entity getEntity(final int number);

    /**
     * Get number of predicates with <code>size</code> number of arguments.
     *
     * @param   size    Number of arguments.
     * @return  Number of predicates in this model.
     */
    public int getPredicateSize(final int size);

    /**
     * Get predicate of this model.
     *
     * @param   size    Number of arguments for predicate.
     * @param   number  Number of predicate.
     * @return  Predicate for this model.
     */
    public Predicate getPredicate(final int size, final int number);

    /**
     * Get predicate constant of this model.
     *
     * @param   con     Predicate constant we are looking for.
     * @return  Predicate for this model.
     */
    public Predicate getPredicateConstant(final PredicateConstant con);

    /**
     * Get number of functions for this model.
     *
     * @param   size    Number of arguments for function.
     * @return  Number of functions in this model.
     */
    public int getFunctionSize(final int size);

    /**
     * Get function.
     *
     * @param   size    Number of arguments for function.
     * @param   number  Number of function.
     * @return  Function in this model.
     */
    public Function getFunction(final int size, final int number);

    /**
     * Get function constant.
     *
     * @param   con     Function constant we are looking for.
     * @return  Function in this model.
     */
    public Function getFunctionConstant(final FunctionConstant con);

    /**
     * Create entity out of entity list. This is a transformation of a list
     * of elements into a class containing these elements.
     *
     * @param   array   List of elements.
     * @return  Class that contains (exactly?) these elements.
     */
    public Entity map(final Entity[] array);

}