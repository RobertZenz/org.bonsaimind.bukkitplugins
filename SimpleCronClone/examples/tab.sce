
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
                - admalledd
            worlds:
    'sample one liner':
        command: 'do say sample one liner $0 $1 $2 $3'
        filters:
            players:
                - admalledd
            worlds:
                - -world


worldNotEmpty:
    'events/worldNotEmpty.sce':
        filters:
            players:
            worlds:


hourChange:
    'sample one liner':
        command: 'do say allevents:::$0:$1:$2'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds:
dawn:
    'sample one liner':
        command: 'do say allevents:::$0:$1'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds:
midday:
    'sample one liner':
        command: 'do say allevents:::$0:$1'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds:
dusk:
    'sample one liner':
        command: 'do say allevents:::$0:$1'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds:
night:
    'sample one liner':
        command: 'do say allevents:::$0:$1'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds:
midnight:
    'sample one liner':
        command: 'do say allevents:::$0:$1'
        filters:
            players: #empty event though not in use/ applicable to this event
            worlds: