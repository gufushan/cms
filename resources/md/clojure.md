# Clojure

## runtime structure
    namespace repo
        namespace
            symbol -> var -> value
                   -> java object
    thread
        reference to namespace repo
        indicator of target namespace

Note: all code executed in thread, below is code and corresponding impact:

* create/remove namespace -> namespace repo
* def* -> namespace
* switch namespace -> indicator of target namespace

## API shape
    CONVERTERS:
        map
            mapcat
            keep-* - fiter answer piece
        replace

    FILTERS:
        filter
        remove
        take-*
        drop-*
        distinct
        dedupe

    OTHER:
        partition-*
        interpose
        interleave
        
    OPERATION THREADER:
        doto - target go through all operations also implicit return
        ->, ->> - target to result to target to result...
