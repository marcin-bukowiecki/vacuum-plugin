/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.notifications

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * @author Marcin Bukowiecki
 */
object VacuumNotifier {

    @JvmStatic
    fun notifyError(project: Project?, title: String, content: String) {
        NotificationGroupManager.getInstance().getNotificationGroup("Vacuum Notification Group")
            .createNotification(title, content, NotificationType.ERROR)
            .notify(project)
    }
}
