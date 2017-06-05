#!/usr/bin/env groovy

class Event{
    def name
    List<Closure> listeners

    void fire(replyTo, args){
        listeners.collect{
            def theArgs = args*.value
            def finalArgs = (theArgs.size() == 1) ? theArgs[0]: theArgs
            def result = it.call(finalArgs)
            replyTo.pushResponse(name, result)
        }
    }
}

class EventDispatcher{
    List<Event> events

    void fire(replyTo, eventName, args){
        events.findAll{it.name == eventName}.each{it.fire(replyTo, args)}
    }
}

interface ResponseQueue{
    void pushResponse(name, result)
    def popResponse(name)
}

class SimpleResponseQueue implements ResponseQueue {
    @Lazy
    def responses = [:]
    
    void pushResponse(name, result){
        responses[name] = result
    }

    def popResponse(name){
        def result = responses[name]
        responses.remove(name)
        return result
    }
}

class NoResponseQueue implements ResponseQueue{
    void pushResponse(name, result){
    }

    def popResponse(name){
        return "nothing" // Should be a neutral value
    }
}

class Speaker{

    @Delegate // Groovy syntax sugar to delegate unknown method calls to the instance following the annotation
    EventDispatcher eventDispatcher

    @Delegate
    ResponseQueue responseQueue

    def name

    void sayHello(){
        fire(this, "capitalizeString", [text: "hello"])
        def capitalized = popResponse("capitalizeString")
        fire(this, "talk", [name: name, text: capitalized])
    }
}


def eventDispatcher = new EventDispatcher(
    events: [
        new Event(name: "talk", listeners: [{
            name, text -> println "$name says $text"
        }]),
        new Event(name: "capitalizeString", listeners:[{
            text -> text.capitalize()
        }])
    ]
)

def speakerWithResponse = new Speaker(name: "Alex", eventDispatcher: eventDispatcher, responseQueue: new SimpleResponseQueue())
speakerWithResponse.sayHello()
// Should say "Alex says Hello"

def speakerWithoutResponse = new Speaker(name: "Alex", eventDispatcher: eventDispatcher, responseQueue: new NoResponseQueue())
speakerWithoutResponse.sayHello()
// Should say "Alex says nothing"
