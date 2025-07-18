import manager.FileBackedTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.TaskStatus;
import tasks.Subtask;
import tasks.Epic;

import java.io.File;

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
    }
}
