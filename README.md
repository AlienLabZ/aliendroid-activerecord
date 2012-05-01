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
Suppose that your project has only one class that you want to map to a table. Let's call this class "Contact":

	public class Contact extends Model {
		public String name;
		public Date birth;
		public Integer age;
	}
	
That's is all you need. No annotations are required. Not even configurations. Now, you can simply do:

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

1. Creating a database named "database.sqlite".
2. Creating one table per Model subclass.
3. Creating table columns based on your model attributes.

Queries
--------
Now you need to load data from your database. With AlienDroid-ActiveRecord you can do this like:

	public MyActivity extends RoboActivity {
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			
			Contact contact = Model.load(Contact.class, 1);
			contact.name = "Changed Name";
			contact.save();
			
			List<Contact> contacts = Model.findAll(Contact.class);
			List<Contact> contacts = Model.where(Contact.class, "name like '?'", new String[] {"name"});
			
			contact.delete();
		}
	}
	
Changing Database Name
----------------------
If you don't like how we named your database file name (database.sqlite), you can change it. Just add those lines to your AndroidManifest file (remember to put it inside <application> tag).

        <meta-data android:name="DATABASE_NAME" android:value="mydatabase.sqlite"/>
        <meta-data android:name="DATABASE_VERSION" android:value="1"/>


	