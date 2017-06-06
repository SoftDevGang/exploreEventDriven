#!/usr/bin/env groovy

class EventDispatcher{
    Map<String, Closure> eventListeners 

    void fire(eventName, args){
        eventListeners[eventName](args*.value)
    }
}

class Speaker{
    EventDispatcher eventDispatcher

    def name

    void sayHello(){
        eventDispatcher.fire("capitalizeString", [speaker: this, text: "hello"])
    }

    void capitalized(capitalizedText){
        eventDispatcher.fire("talk", [name: name, text: capitalizedText])
    }
}

def eventDispatcher = new EventDispatcher(
    eventListeners: [
        capitalizeString: { speaker, text -> speaker.capitalized(text.capitalize()) },
        talk: { name, text -> println "$name says $text"},
    ]
)

def speaker = new Speaker(name: "Alex", eventDispatcher: eventDispatcher)
speaker.sayHello()
// Should say "Alex says Hello"
