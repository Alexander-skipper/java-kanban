import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import manager.exceptions.ManagerSaveException;
import tasks.Task;
import tasks.TaskStatus;
import tasks.Subtask;
import tasks.Epic;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        File dataFile = new File("resources/data.csv");
        TaskManager taskManager = Managers.getDefault();

        // 1. Создать таску
        System.out.println("Создать таску");
        Task task = new Task("Name", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);
        System.out.println(taskManager.getTasks());
        System.out.println("История после создания задачи (должна быть пустая): " + taskManager.getHistory());
        System.out.println();

        // 2. Получить таску (добавится в историю)
        System.out.println("Получить таску");
        System.out.println(taskManager.getTask(task.getId()));
        System.out.println("История после первого просмотра (1 задача): " + taskManager.getHistory());
        System.out.println();

        // 3. Получить таску еще раз (должна остаться одна запись)
        System.out.println("Получить таску повторно");
        System.out.println(taskManager.getTask(task.getId()));
        System.out.println("История после повторного просмотра (все равно 1 задача): " + taskManager.getHistory());
        System.out.println();

        // 4. Создаём Эпик
        System.out.println("Создаём Эпик");
        Epic epic = new Epic("Отпуск", "Организовать отпуск");
        taskManager.createEpic(epic);
        System.out.println("История после создания эпика (не должна измениться): " + taskManager.getHistory());
        System.out.println();

        // 5. Просматриваем эпик (добавится в историю)
        System.out.println("Просматриваем эпик");
        System.out.println(taskManager.getEpic(epic.getId()));
        System.out.println("История после просмотра эпика (2 задачи): " + taskManager.getHistory());
        System.out.println();

        // 6. Создаём Сабтаски
        System.out.println("Создаём Сабтаски");
        Subtask subtask1 = new Subtask("Путёвка", "Выбрать путёвку", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Билеты", "Купить билеты", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        System.out.println("История после создания подзадач (не должна измениться): " + taskManager.getHistory());
        System.out.println();

        // 7. Просматриваем подзадачи (добавляются в историю)
        System.out.println("Просматриваем подзадачи");
        System.out.println("Подзадача 1: " + taskManager.getSubtasks(subtask1.getId()));
        System.out.println("Текущая история (3 задачи - задача, эпик, подзадача1): " + taskManager.getHistory());
        System.out.println("Подзадача 2: " + taskManager.getSubtasks(subtask2.getId()));
        System.out.println("История после просмотра второй подзадачи (4 задачи): " + taskManager.getHistory());
        System.out.println();

        // 8. Проверяем сохранение в файл.
        System.out.println("Проверка работы с файлом:");
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(dataFile);
        System.out.println("Загруженные задачи: " + loadedManager.getTasks());
        System.out.println("Загруженные эпики: " + loadedManager.getEpics());
        System.out.println("Загруженные сабтаски: " + loadedManager.getSubtasks());
        System.out.println();

        // 9. Удаляем таску (должна удалиться из истории)
        System.out.println("Удаляем таску");
        taskManager.deleteTask(task.getId());
        System.out.println("История после удаления задачи (3 задачи): " + taskManager.getHistory());
        System.out.println();

        // 10. Удаляем Эпик (должны удалиться эпик и его подзадачи из истории)
        System.out.println("Удаляем Эпик");
        taskManager.deleteEpic(epic.getId());
        System.out.println("История после удаления эпика (должна быть пустая): " + taskManager.getHistory());
        System.out.println();

        // 11. Проверяем историю после всех операций.
        System.out.println("Финальная проверка истории");
        System.out.println("История: " + taskManager.getHistory());
        System.out.println();

        // 12. Проверяем удаление из файла.
        System.out.println("Проверка работы с файлом:");
        TaskManager newLoadedManager = FileBackedTaskManager.loadFromFile(dataFile);
        System.out.println("Загруженные задачи: " + newLoadedManager.getTasks());
        System.out.println("Загруженные эпики: " + newLoadedManager.getEpics());
        System.out.println("Загруженные сабтаски: " + newLoadedManager.getSubtasks());
        System.out.println();

        // 13. Создать таску с временными параметрами.
        System.out.println("Создаем задачу с временным интервалом");
        LocalDateTime taskTime = LocalDateTime.now().plusHours(1);
        Task task2 = new Task("Name", "Desc", TaskStatus.NEW,
                taskTime, Duration.ofMinutes(30));
        taskManager.createTask(task2);
        System.out.println("Задача создана: " + task2);
        System.out.println("Время задачи: " + task2.getStartTime() + " - " + task2.getEndTime());
        System.out.println();

        // 14. Проверка пересечения задач
        System.out.println("Проверка запрета пересечения задач");
        Task overlappingTask = new Task("Пересекающаяся", "Описание", TaskStatus.NEW,
                taskTime.plusMinutes(15), Duration.ofMinutes(30));
        try {
            taskManager.createTask(overlappingTask);
            System.err.println("ОШИБКА: Система разрешила создать пересекающуюся задачу!");
        } catch (ManagerSaveException e) {
            System.out.println("КОРРЕКТНО: " + e.getMessage());
            System.out.println("Пересекающиеся задачи не добавлены - система работает правильно");
        }
        System.out.println();

        // 15. Создаём Эпик с подзадачами с временем.
        System.out.println("Создаем эпик с временными подзадачами");
        Epic epic2 = new Epic("Отпуск", "Организовать отпуск");
        taskManager.createEpic(epic2);

        LocalDateTime subtask1Time = LocalDateTime.now().plusDays(1);
        Subtask subtask3 = new Subtask("Путёвка", "Выбрать путёвку", TaskStatus.NEW,
                epic2.getId(), subtask1Time, Duration.ofHours(2));

        LocalDateTime subtask2Time = subtask1Time.plusHours(3);
        Subtask subtask4 = new Subtask("Билеты", "Купить билеты", TaskStatus.NEW,
                epic2.getId(), subtask2Time, Duration.ofHours(1));

        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);

        System.out.println("Время эпика: " + epic2.getStartTime() + " - " + epic2.getEndTime());
        System.out.println("Длительность эпика: " +
                taskManager.getEpicDuration(epic2.getId()).toHours() + " часов");
        System.out.println();

        // 16. Проверка пересечения с подзадачами
        System.out.println("Проверка запрета пересечения с подзадачами");
        Task overlappingWithSubtask = new Task("Пересекается с подзадачей", "Описание",
                TaskStatus.NEW, subtask1Time.plusMinutes(30),
                Duration.ofHours(1));
        try {
            taskManager.createTask(overlappingWithSubtask);
            System.err.println("ОШИБКА: Система разрешила пересечение с подзадачей!");
        } catch (ManagerSaveException e) {
            System.out.println("КОРРЕКТНО: " + e.getMessage());
            System.out.println("Пересечения с подзадачами запрещены - система работает правильно");
        }
        System.out.println();

        // 17. Проверка НЕпересекающихся задач
        System.out.println("Проверка создания НЕпересекающихся задач");
        Task nonOverlappingTask = new Task("Непересекающаяся", "Описание", TaskStatus.NEW,
                subtask1Time.plusHours(5), Duration.ofHours(1));
        try {
            taskManager.createTask(nonOverlappingTask);
            System.out.println("КОРРЕКТНО: Непересекающаяся задача создана успешно");
            System.out.println("Все задачи: " + taskManager.getTasks());
        } catch (ManagerSaveException e) {
            System.err.println("ОШИБКА: " + e.getMessage());
        }
    }
}