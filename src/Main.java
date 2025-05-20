import tasks.Task;
import tasks.TaskStatus;
import tasks.Subtask;
import tasks.Epic;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();


        // 1.Создать таску
        System.out.println("Создать таску");
        Task taskForCreate = new Task("Name", "Desc", TaskStatus.NEW);
        taskManager.createTask(taskForCreate);
        System.out.println(taskManager.getTasks());
        System.out.println();

        // 2.Получить таску
        System.out.println("Получить таску");
        System.out.println(taskManager.getTask(taskForCreate.getId()));
        System.out.println();

        // 3. Проверим обновление
        System.out.println("Проверим обновление");
        Task taskForUpdate = new Task(taskForCreate.getId(), "New name",
                taskForCreate.getDescription(), TaskStatus.IN_PROGRESS);
        taskForUpdate = taskManager.updateTask(taskForUpdate);
        System.out.println(taskManager.getTasks());
        System.out.println();

        //4. Удаляем таску
        System.out.println("Удаляем таску");
        taskManager.deleteTask(taskForUpdate.getId());
        System.out.println(taskManager.getTasks());
        System.out.println();

        //5. Создаём Эпик
        System.out.println("Создаём Эпик");
        Epic epic = new Epic("Отпуск", "Организовать отпуск");
        taskManager.createEpic(epic);
        System.out.println(taskManager.getEpic(epic.getId()));
        System.out.println();

        //5. Создаём Сабтаски
        System.out.println("Создаём Сабтаски");
        Subtask subtask1 = new Subtask("Путёвка", "Выбрать путёвку", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Билеты", "Купить билеты", TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.getEpicSubtasks(epic.getId());
        System.out.println(taskManager.getEpicSubtasks(epic.getId()));
        System.out.println();

        //6. Проверяем статус Эпика (NEW)
        System.out.println("Проверяем статус Эпика " + taskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //7. Меняем статус первого Сабтаска.
        Subtask updatedSubtask = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                TaskStatus.DONE, subtask1.getEpicId());
        taskManager.updateSubtask(updatedSubtask);
        System.out.println("Статус Эпика после изменения одной Сабтаски " + taskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //8.  Меняем статус второго Сабтаска.
        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(),
                TaskStatus.DONE, subtask2.getEpicId());
        taskManager.updateSubtask(updatedSubtask2);
        System.out.println("Статус Эпика после изменения одной Сабтаски " + taskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //9. Удаляем Эпик.
        taskManager.deleteEpic(epic.getId());
        System.out.println("Эпики после удаления: " + taskManager.getEpics());
        System.out.println("Сабтаски после удаления Эпика: " + taskManager.getSubtasks());
        System.out.println();

        //10. Проверяем удаление всех задач
        taskManager.deleteAllTasks();
        System.out.println("Все Таски после очистки: " + taskManager.getTasks());
        System.out.println("Все Сабтаски после очистки: " + taskManager.getSubtasks());
        System.out.println("Все Эпики после очистки: " + taskManager.getEpics());
    }
}
