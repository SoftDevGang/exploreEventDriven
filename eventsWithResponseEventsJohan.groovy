#!/usr/bin/env groovy


class EventDispatcher{
    Map<String, Closure> eventListeners = new HashMap();

    void fire(eventName, event){

        eventListeners.get(eventName).each{it.call(event)}
    }

    void addListener(String eventName, Closure listener) {

        if (isEmpty(eventListeners.get(eventName))) {
            eventListeners.put(eventName, [listener])
        }
        else {
            eventListeners.get(eventName).add(listener)
        }


    }
}

class Speaker{
    EventDispatcher eventDispatcher

    def name

    void sayHello(){
        eventDispatcher.fire("capitalizeString", [text: "hello"])
    }

    void capitalized(capitalizedText){
        println "$name says $capitalizedText"
    }
}


def eventDispatcher = new EventDispatcher()


def speaker = new Speaker(name: "Alex", eventDispatcher: eventDispatcher)
eventDispatcher.addListener("capitalizeString", text -> speaker.capitalized(text));
speaker.sayHello()
// Should say "Alex says Hello"
