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
      (doseq [[x z] [[-1 0] [0 -1] [1 0] [0 1] [0 0]]]
        (let [b (.getBlock (.add (.clone (.getLocation block)) x 0 z))]
          (cond
            (= type Material/STONE) (.setType b Material/COBBLESTONE)
            (= type Material/COBBLESTONE) (.setType b Material/AIR))))
      (.remove entity))))

(defn projectile-hit-event [evt]
  (let [entity (.getEntity evt)]
    (when (instance? Arrow entity)
      (arrow-hit-event evt entity))))

(defn arrow-damages-entity-event [evt attacker target]
  (let [loc (.add (.clone (.getLocation target)) 0 -1 0)]
    (doseq [[x z] (vec (concat (map (fn [i] [i 0]) (range -2 3)) (map (fn [i] [0 i]) (range -2 3))))]
      (let [b (.getBlock (.add (.clone loc) x 0 z))
            type (.getType b)]
        (cond
          (= type Material/STONE) (.setType b Material/COBBLESTONE)
          (= type Material/COBBLESTONE) (.setType b Material/AIR)))))
  (.setCancelled evt true))

(defn entity-damage-event [evt]
  (let [target (.getEntity evt)
        attacker (when (instance? EntityDamageByEntityEvent evt)
                   (.getDamager evt))]
    (when (and attacker
               (instance? Arrow attacker)
               (.getShooter attacker)
               (instance? Player (.getShooter attacker)))
      (arrow-damages-entity-event evt attacker target))))

(defonce swank* nil)
(defn on-enable [plugin]
  (when (nil? swank*)
    (def swank* (swank.swank/start-repl 4008))))
