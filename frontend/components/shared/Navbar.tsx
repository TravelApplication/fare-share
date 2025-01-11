import Image from "next/image";
import Link from "next/link";

function Navbar() {
  // hardcoded values
  // TODO: replace with actual values
  const isLogged = true;
  return (
    <nav className="navbar">
      <div className="navbar_content">
        <Link href="/" className="-ml-1 flex items-center gap-1.5">
          <Image src="/assets/logo.svg" alt="logo" width={44} height={44} />
          <p className="text-heading3-bold max-xs:hidden">FareShare</p>
        </Link>
        <div className="flex items-center gap-6">
          {isLogged ? (
            <>
              <Link className="navbar_link" href="/trips">
                <p>Trips</p>
              </Link>

              <Link className="navbar_link" href="/notifications">
                <Image
                  src="/assets/notification.svg"
                  alt="notifications"
                  width={24}
                  height={24}
                />
                <p className="max-sm:hidden">Notifications</p>
              </Link>
              <Link className="navbar_link" href="/account">
                <Image
                  src="/assets/profile.svg"
                  alt="profile"
                  width={24}
                  height={24}
                />
                <p className="max-sm:hidden">Account</p>
              </Link>
            </>
          ) : (
            <>
              <Link className="navbar_link" href="/sign-in">
                Sign In
              </Link>
              <button className="light-btn">
                <Link href="/sign-up">Sign Up - It's Free</Link>
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
