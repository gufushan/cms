# Recursion

## Definition
One stuff contains another stuff of the same type

## Details
### Can be created via:

* loop between: outside inside
* replace reference in function which contains a reference of itself

### Simple Example:

    (((((0
    
Note:  
These '('s are isomorphic  
If the '('s are the same, it's a dead loop in program  
This structure can be used to ship repeated stuff like: (x(x(x(x...  
U can divide this structure on ur favor. E.g.: (x, (x, (x, ... or (x(x(x(x(x, (x(x(x(x, (x(x(x, ...  

### Function Recursion(Declarative View):

    A --          -- Q
     Ai --      -- Qi
      Aii --  -- Qii
            ...
      Abase == Qbase

Note:  
A, Ai, Aii... are answers  
Q, Qi, Qii... are questions  
--- are relations(functions)

### Procedure Recursion(Imperative View):

Recursively apply a process which:

1. have Answer and Problem as input
2. eat Problem Piece and generate Answer Piece
3. add Answer Piece to Answer
