
playerJoin:
    - file: 'events/playerJoin.sce'
      filters:
          players:
              - admalledd
              - -notch
          worlds:
              - -hub
    - command: 'do say allevents:::$0:$1'
      filters:
          players:
          worlds:
    - file: 'events/playerJoin.sce'
      filters:
          players:
          worlds:

playerFirstJoin:
    - file: 'events/playerFirstJoin.sce'
      filters:
          players:
          worlds:

playerQuit:
    - file: 'events/playerQuit.sce'
      filters:
          players:
          worlds:


serverEmpty:
    - file: 'events/serverEmpty.sce'
      filters:
          players:
          worlds:
  
  
serverNotEmpty:
    - file: 'events/serverNotEmpty.sce'
      filters:
          players:
          worlds:
  
playerTeleportWorld:
    - file: 'events/playerTeleportWorld.sce'
      filters:
          players:
          worlds:
  
  
worldEmpty:
    - file: 'events/worldEmpty.sce'
      filters:
          players:
              - admalledd
          worlds:
    - command: 'do say sample one liner $0 $1 $2 $3'
      filters:
          players:
              - admalledd
          worlds:
              - -world
  
  
worldNotEmpty:
    - file: 'events/worldNotEmpty.sce'
      filters:
          players:
          worlds:
  
  
hourChange:
    - command: 'do say allevents:::$0:$1:$2'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds:
dawn:
    - command: 'do say allevents:::$0:$1'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds:
midday:
    - command: 'do say allevents:::$0:$1'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds:
dusk:
    - command: 'do say allevents:::$0:$1'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds:
night:
    - command: 'do say allevents:::$0:$1'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds:
midnight:
    - command: 'do say allevents:::$0:$1'
      filters:
          players: #empty event though not in use/ applicable to this event
          worlds: