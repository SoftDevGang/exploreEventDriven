#!/usr/bin/env groovy

class Event{
    def name
    def replyName
    List<Closure> listeners

    void fire(eventDispatcher, args){
        listeners.collect{
            def theArgs = args*.value
            def finalArgs = (theArgs.size() == 1) ? theArgs[0]: theArgs
            def result = it.call(finalArgs)
            eventDispatcher.fire(replyName, [text: result])
        }
    }
}

class EventDispatcher{
    List<Event> events

    void fire(eventName, args){
        events.findAll{it.name == eventName}.each{it.fire(this, args)}
    }
}

class Speaker{
    EventDispatcher eventDispatcher

    def name

    void sayHello(){
        eventDispatcher.fire("capitalizeString", [text: "hello"])
    }

    void capitalized(capitalizedText){
        eventDispatcher.fire("talk", [name: name, text: capitalizedText])
    }
}


def eventDispatcher = new EventDispatcher(
    events: [
        new Event(name: "talk", listeners: [{
            name, text -> println "$name says $text"
        }]),
        new Event(name: "capitalizeString", replyName: "stringCapitalized", listeners:[{
            text -> text.capitalize()
        }])
    ]
)


def speaker = new Speaker(name: "Alex", eventDispatcher: eventDispatcher)
eventDispatcher.events.add(new Event(name: "stringCapitalized", listeners: [{text -> speaker.capitalized(text)}]))
speaker.sayHello()
// Should say "Alex says Hello"
