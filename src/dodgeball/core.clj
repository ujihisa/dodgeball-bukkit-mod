(ns dodgeball.core
  (:require [cloft.cloft :as c])
  (:require [swank.swank])
  (:require [clojure.set :as s])
  (:import [org.bukkit Bukkit Material])
  (:import [org.bukkit.entity Animals Arrow Blaze Boat CaveSpider Chicken
            ComplexEntityPart ComplexLivingEntity Cow Creature Creeper Egg
            EnderCrystal EnderDragon EnderDragonPart Enderman EnderPearl
            EnderSignal ExperienceOrb Explosive FallingSand Fireball Fish
            Flying Ghast Giant HumanEntity Item LightningStrike LivingEntity
            MagmaCube Minecart Monster MushroomCow NPC Painting Pig PigZombie
            Player PoweredMinecart Projectile Sheep Silverfish Skeleton Slime
            SmallFireball Snowball Snowman Spider Squid StorageMinecart
            ThrownPotion TNTPrimed Vehicle Villager WaterMob Weather Wolf
            Zombie])
  (:import [org.bukkit.inventory ItemStack])
  (:import [org.bukkit.event.entity EntityDamageByEntityEvent
            EntityDamageEvent$DamageCause]))

(defn arrow-hit-event [evt entity]
  (when (instance? Player (.getShooter entity))
    (let [location (.getLocation entity)
          world (.getWorld location)
          velocity (.getVelocity entity)
          direction (.multiply (.clone velocity) (double (/ 1 (.length velocity))))
          block (.getBlock (.add (.clone location) direction))
          type (.getType block)]
      (cond
        (= type Material/STONE) (.setType block Material/COBBLESTONE)
        (= type Material/COBBLESTONE) (.setType block Material/AIR)))))

(defn projectile-hit-event [evt]
  (let [entity (.getEntity evt)]
    (when (instance? Arrow entity)
      (arrow-hit-event evt entity))))

(defonce swank* nil)
(defn on-enable [plugin]
  (when (nil? swank*)
    (def swank* (swank.swank/start-repl 4008))))
