#!/usr/bin/env groovy

class Event{
    def name

    def text
    def origin

    List<Closure> listeners

    def fire(args){
        return listeners.collect{
            def theArgs = args*.value
            def finalArgs = (theArgs.size() == 1) ? theArgs[0]: theArgs
            it.call(finalArgs)
        }
    }
}

class EventDispatcher{
    List<Event> events

    def fire(eventName, args){
        return events.findAll{it.name == eventName}.collect{it.fire(args)}
    }
}


class Speaker{
    def eventDispatcher
    def name

    void sayHello(){
        def capitalized = eventDispatcher.fire("capitalizeString", [text: "hello"]).first().first()
        eventDispatcher.fire("talk", [origin: name, text: capitalized])
    }
}

def eventDispatcher = new EventDispatcher(
    events: [
        new Event(name: "talk", listeners: [{
            origin, text -> println "$origin says $text"
        }]),
        new Event(name: "capitalizeString", listeners:[{
            text -> text.capitalize()
        }])
    ]
)

def s = new Speaker(name: "Alex", eventDispatcher: eventDispatcher)

s.sayHello()

// Should say "Alex says Hello"
