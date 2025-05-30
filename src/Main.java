import tasks.Task;
import tasks.TaskStatus;
import tasks.Subtask;
import tasks.Epic;

public class Main {

    public static void main(String[] args) {

        InMemoriTaskManager inMemoriTaskManager = new InMemoriTaskManager();


        // 1.Создать таску
        System.out.println("Создать таску");
        Task taskForCreate = new Task("Name", "Desc", TaskStatus.NEW);
        inMemoriTaskManager.createTask(taskForCreate);
        System.out.println(inMemoriTaskManager.getTasks());
        System.out.println();

        // 2.Получить таску
        System.out.println("Получить таску");
        System.out.println(inMemoriTaskManager.getTask(taskForCreate.getId()));
        System.out.println();

        // 3. Проверим обновление
        System.out.println("Проверим обновление");
        Task taskForUpdate = new Task(taskForCreate.getId(), "New name",
                taskForCreate.getDescription(), TaskStatus.IN_PROGRESS);
        taskForUpdate = inMemoriTaskManager.updateTask(taskForUpdate);
        System.out.println(inMemoriTaskManager.getTasks());
        System.out.println();

        //4. Удаляем таску
        System.out.println("Удаляем таску");
        inMemoriTaskManager.deleteTask(taskForUpdate.getId());
        System.out.println(inMemoriTaskManager.getTasks());
        System.out.println();

        //5. Создаём Эпик
        System.out.println("Создаём Эпик");
        Epic epic = new Epic("Отпуск", "Организовать отпуск");
        inMemoriTaskManager.createEpic(epic);
        System.out.println(inMemoriTaskManager.getEpic(epic.getId()));
        System.out.println();

        //5. Создаём Сабтаски
        System.out.println("Создаём Сабтаски");
        Subtask subtask1 = new Subtask("Путёвка", "Выбрать путёвку", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Билеты", "Купить билеты", TaskStatus.NEW, epic.getId());
        inMemoriTaskManager.createSubtask(subtask1);
        inMemoriTaskManager.createSubtask(subtask2);
        inMemoriTaskManager.getEpicSubtasks(epic.getId());
        System.out.println(inMemoriTaskManager.getEpicSubtasks(epic.getId()));
        System.out.println();

        //6. Проверяем статус Эпика (NEW)
        System.out.println("Проверяем статус Эпика " + inMemoriTaskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //7. Меняем статус первого Сабтаска.
        Subtask updatedSubtask = new Subtask(subtask1.getId(), subtask1.getName(), subtask1.getDescription(),
                TaskStatus.DONE, subtask1.getEpicId());
        inMemoriTaskManager.updateSubtask(updatedSubtask);
        System.out.println("Статус Эпика после изменения одной Сабтаски " + inMemoriTaskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //8.  Меняем статус второго Сабтаска.
        Subtask updatedSubtask2 = new Subtask(subtask2.getId(), subtask2.getName(), subtask2.getDescription(),
                TaskStatus.DONE, subtask2.getEpicId());
        inMemoriTaskManager.updateSubtask(updatedSubtask2);
        System.out.println("Статус Эпика после изменения одной Сабтаски " + inMemoriTaskManager.getEpic(epic.getId()).getTaskStatus());
        System.out.println();

        //9. Удаляем Эпик.
        inMemoriTaskManager.deleteEpic(epic.getId());
        System.out.println("Эпики после удаления: " + inMemoriTaskManager.getEpics());
        System.out.println("Сабтаски после удаления Эпика: " + inMemoriTaskManager.getSubtasks());
        System.out.println();

        //10. Проверяем удаление всех задач
        inMemoriTaskManager.deleteAllTasks();
        System.out.println("Все Таски после очистки: " + inMemoriTaskManager.getTasks());
        System.out.println("Все Сабтаски после очистки: " + inMemoriTaskManager.getSubtasks());
        System.out.println("Все Эпики после очистки: " + inMemoriTaskManager.getEpics());
    }
}
