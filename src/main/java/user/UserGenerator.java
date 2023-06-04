package user;

import user.User;

public class UserGenerator {

    public static User getDefault() {
        return new User("Isido_Uryu@yandex.ru", "password", "Testen4");
    }

    public static User getUserWithoutNecessaryField() {
        return new User("IchigoKurasakiS@yandex.ru", "password"); // для тестирования кейса с отсутствующим обязательным полем
    }

    public static User getSpecificUser() {
        return new User("Mrlogin@mail.ru", "123456789", "Tester"); // Пользователь для тестирования авторизации, его во время тестирования не удалять
    }

    public static User getSpecificUserWithNotRightLogin() {
        return new User("Mslogin@mail.ru", "987654321", "Tester"); // Пользователь для тестирования авторизации, с неправильными логином и паролем
    }

    public static User getSpecificUserForChangeData() {
        return new User("MsChange@mail.ru", "87654321", "Tester Four"); // Пользователь для тестирования авторизации, с неправильными логином и паролем
    }

}
