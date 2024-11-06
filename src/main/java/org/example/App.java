package org.example;

import org.example.model.Item;
import org.example.model.Person;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        Configuration configuration = new Configuration().addAnnotatedClass(Person.class).addAnnotatedClass(Item.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        try (sessionFactory) {
            Session session = sessionFactory.getCurrentSession();
                session.beginTransaction();

            Person person = session.get(Person.class, 1);
            System.out.println("Получили человека");

//            Hibernate.initialize(person.getItems()); // Подгружаем ленивые сущности

                session.getTransaction().commit();

            System.out.println("Сессия закончилась");

            // Открываем сессию и транкзацию еще раз (Можно сделать в любом другом месте в коде)
            session = sessionFactory.getCurrentSession();
                session.beginTransaction();

            System.out.println("Внутри второй транзакции");

//            person = session.merge(person); Если через HQL то можно не делать merge

//            Hibernate.initialize(person.getItems()); // Подгружаем ленивые сущности

            // Так тоже можно, но это больше код, поэтому лучше как сверху, просто тут HQL код
            List<Item> items = session.createQuery("select i from Item i where i.owner.id = :person_id", Item.class)
                            .setParameter("person_id", person.getId()).getResultList();

            System.out.println(items);

                session.getTransaction().commit();

            System.out.println("Вне второй сессии");

            // Это работает так как связанные товары были подгружены
//            System.out.println(person.getItems());
        }

    }
}
