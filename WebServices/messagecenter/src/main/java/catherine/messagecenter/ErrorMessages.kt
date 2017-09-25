package catherine.messagecenter

/**
 * Created by Catherine on 2017/9/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
class ErrorMessages {
    companion object {
        /**
         * Action names or messages are null.
         */
        val NULL_POINTER = 100

        /**
         * You can't send multiple types of broadcast messages with same actionName at the same time.
         * You need to rename this action.
         */
        val MULTIPLE_VALUE = 101
    }
}