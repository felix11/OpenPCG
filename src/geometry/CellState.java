package geometry;

/**
 * The state of one cell.
 * Can be EMPTY(' '), PERSON('p'), OBSTACLE('#'), SOURCE('s') and TARGET('t').
 * 
 * @author Felix Dietrich
 */
public enum CellState
{
    EMPTY(' '), PERSON('p'), OBSTACLE('#'), SOURCE('s'), TARGET('t');
    final private char shortForm;

    CellState(char c)
    {
        shortForm = c;
    }

    public static CellState valueOf(char c)
    {
        switch (c)
        {
            case ' ':
                return CellState.EMPTY;
            case 'p':
                return CellState.PERSON;
            case 't':
                return CellState.TARGET;
            case '#':
                return CellState.OBSTACLE;
        }
        return CellState.EMPTY;
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(shortForm);

        return result.toString();
    }
}
