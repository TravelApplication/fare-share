import Link from "next/link";

export default function Home() {
  return (
    <div className="section py-6 px-12">
      <div>
        <h1 className="text-heading1-bold">Group travel, made simple.</h1>
        <p className="mt-4">
          Plan, vote on activities, and keep everything organized with
          FareShare. From destinations to shared expenses — make every trip
          effortless and unforgettable, together.
        </p>
      </div>
      <button className="primary-btn text-heading4-medium px-16 py-3 mt-4">
        <Link href="/sign-up">Get Started</Link>
      </button>
    </div>
  );
}
