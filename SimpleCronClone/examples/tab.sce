
playerJoin:
    'events/playerJoin.sce':
        filters:
            players:
                #event must match player name
                - admalledd
                #negated, so do NOT match this
                - -notch
            worlds:
                #same kind of thing here, negation regex ect...
                - -hub


playerFirstJoin:
    'events/playerFirstJoin.sce':
        filters:
            players:
            worlds:

playerQuit:
    'events/playerQuit.sce':
        filters:
            players:
            worlds:


serverEmpty:
    'events/serverEmpty.sce':
        filters:
            players:
            worlds:


serverNotEmpty:
    'events/serverNotEmpty.sce':
        filters:
            players:
            worlds:

playerTeleportWorld:
    'events/playerTeleportWorld.sce':
        filters:
            players:
            worlds:


worldEmpty:
    'events/worldEmpty.sce':
        filters:
            players:
            worlds:


worldNotEmpty:
    'events/worldNotEmpty.sce':
        filters:
            players:
            worlds:
