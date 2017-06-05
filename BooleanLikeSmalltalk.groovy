#!/usr/bin/env groovy

abstract class MyBoolean{
    abstract def ifTrue(Closure trueBlock)
    abstract def ifFalse(Closure falseBlock)
}

class True extends MyBoolean {
    def ifTrue(Closure trueBlock) {
        trueBlock()
        return this
    }

    def ifFalse(Closure falseBlock) {
        return this
    }
}

class False extends MyBoolean {
    def ifTrue(Closure trueBlock){
        return this
    }

    def ifFalse(Closure falseBlock){
        falseBlock()
        return this
    }
}

Boolean.metaClass.asType = {Class clazz -> return delegate ? new True() : new False() }


((5 > 4) as MyBoolean).ifTrue{ println "5 > 4 is TRUE"}.ifFalse {println "SHOULDN'T PRINT THIS"}
((5 > 6) as MyBoolean).ifTrue{ println "SHOULDN'T PRINT THIS"}.ifFalse{ println "5 > 6 is FALSE"}
