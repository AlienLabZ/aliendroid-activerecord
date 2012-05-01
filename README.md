AlienDroid - ActiveRecord
=========================

Install
----------
If you use Maven, just add these dependency:

	<dependency>
		<groupId>com.alienlabz</groupId>
		<artifactId>aliendroid-activerecord</artifactId>
		<version>1.0.0</version>
	</dependency>

But if you don't use Maven, just go to our Download section and get aliendroid-core.jar and aliendroid-activerecord.jar. Add those jars to your dependencies in your Android project. You will need RoboGuice and his specifics dependencies too.

Using
------
Suppose that you want to persist one class. Let's call this class as "Contact". You can create the class like this:

	public class Contact extends Model {
		public String name;
		public Date birth;
		public Integer age;
	}
	
That's is all you need. Now, you can do:

	public MyActivity extends RoboActivity {
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			Contact contact = new Contact();
			contact.name = "My Name";
			contact.birth = new Date();
			contact.age = 21;
			contact.save();
		}
	}

So, do you like to know what AlienDroid is doing behind the scenes?
1. Create a database named "database.sqlite";
2. Create one table per Model subclass;
3. Create table columns based on your model attributes.